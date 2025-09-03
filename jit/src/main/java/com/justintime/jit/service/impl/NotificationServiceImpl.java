package com.justintime.jit.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.justintime.jit.dto.OrderDTO;
import com.justintime.jit.dto.OrderItemDTO;
import com.justintime.jit.entity.DiningTable;
import com.justintime.jit.dto.SubscriptionRequest;
import com.justintime.jit.entity.Enums.Role;
import com.justintime.jit.entity.OrderEntities.OrderItem;
import com.justintime.jit.entity.PushSubscription;
import com.justintime.jit.entity.User;
import com.justintime.jit.exception.ResourceNotFoundException;
import com.justintime.jit.repository.PushSubscriptionRepository;
import com.justintime.jit.service.NotificationService;
import com.justintime.jit.service.PushService;
import com.justintime.jit.service.UserService;
import com.justintime.jit.service.WebPushService;
import com.justintime.jit.util.mapper.GenericMapper;
import com.justintime.jit.util.mapper.MapperFactory;

import lombok.Getter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.justintime.jit.util.constants.JITConstants.*;

@Service
public class NotificationServiceImpl extends BaseServiceImpl<PushSubscription, Long> implements NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationServiceImpl.class);

    private final PushService webSocketPushService;
    private final WebPushService webPushService;
    private final PushSubscriptionRepository subscriptionRepository;
    private final ObjectMapper objectMapper;
    private final UserService userService;

    private final GenericMapper<OrderItem, OrderItemDTO> orderItemMapper = MapperFactory.getMapper(OrderItem.class,
            OrderItemDTO.class);

    public NotificationServiceImpl(PushService webSocketPushService,
            WebPushService webPushService,
            PushSubscriptionRepository subscriptionRepository,
            ObjectMapper objectMapper, UserService userService) {
        this.webSocketPushService = webSocketPushService;
        this.webPushService = webPushService;
        this.subscriptionRepository = subscriptionRepository;
        this.objectMapper = objectMapper;
        this.userService = userService;
    }

    @Override
    public void notifyOrderItemCreation(List<OrderItem> orderItems) {
        orderItems.forEach(this::notifyOrderItemCreationToUser);
    }

    // TODO Enhance this so that if we subscribe once we send the notifications

    @Override
    public void notifyOrderItemStatusUpdate(OrderItem orderItem) {
        notifyOrderItemStatusUpdateToUser(orderItem);
        broadcastOrderItemStatusUpdate(orderItem, Role.ADMIN.name());
    }

    @Override
    public void notifyTableAvailabilityUpdate(List<DiningTable> diningTables) {
        broadcastTableAvailability(diningTables);
    }

    @Override
    public void notifyOrderStatusUpdate(OrderDTO order) {
        notifyOrderStatusUpdateToUser(order);
    }

    @Override
    public void subscribePushNotifications(SubscriptionRequest subscriptionDetails) {
        PushSubscription pushSubscription = new PushSubscription();
        pushSubscription.setEndpoint(subscriptionDetails.getEndpoint());
        pushSubscription.setP256dh(subscriptionDetails.getKeys().getP256dh());
        pushSubscription.setAuth(subscriptionDetails.getKeys().getAuth());
        User user = userService.findByEmail(getUsernameFromJWTBean());
        if (user == null) {
            throw new ResourceNotFoundException("User not found");
        }
        pushSubscription.setUser(user);
        subscriptionRepository.save(pushSubscription);
    }

    private void notifyOrderItemCreationToUser(OrderItem orderItem) {
        User cook = orderItem.getCook();
        OrderItemDTO orderItemDTO = orderItemMapper.toDto(orderItem);
        webSocketPushService.sendToUser(cook.getEmail(), ORDER_ITEM_CREATED_EVENT, orderItemDTO);
        // For now lets keep it like this, TODO Add a Shared Registry to check if the
        // websocket is connected or not and then send this notification
        var payload = new NotificationPayload("New Item Assigned",
                "You have a new item to prepare: " + orderItem.getMenuItem().getName());
        sendOfflineNotificationToUser(cook.getId(), payload);
    }

    private void notifyOrderItemStatusUpdateToUser(OrderItem orderItem) {
        User user = orderItem.getOrder().getUser();
        OrderItemDTO orderItemDTO = orderItemMapper.toDto(orderItem);
        webSocketPushService.sendToUser(user.getEmail(), ORDER_ITEM_STATUS_UPDATED_EVENT, orderItemDTO);
        // For now lets keep it like this, TODO Add a Shared Registry to check if the
        // websocket is connected or not and then send this notification
        var payload = new NotificationPayload(
                "Order Status Update",
                "Item '" + orderItem.getMenuItem().getName() + "' is now " + orderItem.getOrderItemStatus().name());
        sendOfflineNotificationToUser(user.getId(), payload);
    }

    private void sendOfflineNotificationToUser(Long userId, NotificationPayload payload) {
        List<PushSubscription> subscriptions = subscriptionRepository.findByUserId(userId);

        if (subscriptions.isEmpty()) {
            return;
        }
        try {
            String payloadJson = objectMapper.writeValueAsString(payload);
            logger.info("Sending Web Push notification to {} device(s) for user ID: {}", subscriptions.size(), userId);
            subscriptions.forEach(sub -> webPushService.sendNotification(sub, payloadJson));
        } catch (JsonProcessingException e) {
            logger.error("Failed to serialize Web Push payload for user ID: {}", userId, e);
        }
    }

    private void notifyOrderStatusUpdateToUser(OrderDTO order) {
        String email = order.getServerEmail();
        // OrderStatusUpdateDTO orderStatusUpdateDTO = new
        // OrderStatusUpdateDTO(order.getOrderNumber(), order.getStatus());
        webSocketPushService.sendToUser(email, ORDER_ITEM_STATUS_UPDATED_EVENT, order);
    }

    private void broadcastOrderItemStatusUpdate(OrderItem orderItem, String role) {
        String restaurantCode = orderItem.getOrder().getRestaurant().getRestaurantCode();
        OrderItemDTO orderItemDTO = orderItemMapper.toDto(orderItem);
        webSocketPushService.broadcastToRole(restaurantCode, role, ORDER_ITEM_STATUS_UPDATED_EVENT, orderItemDTO);
    }

    private void broadcastTableAvailability(List<DiningTable> diningTables) {
        String restaurantCode = "";
        if (diningTables.stream().findFirst().isPresent()) {
            restaurantCode = diningTables.stream().findFirst().get().getRestaurant().getRestaurantCode();
        }
        if (StringUtils.isBlank(restaurantCode))
            throw new RuntimeException("Issue at NotificationServiceImpl, Restaurant code is not found");
        webSocketPushService.broadcastToRole(restaurantCode, Role.SERVER.name(), TABLE_AVAILABILITY_EVENT,
                diningTables);
        webSocketPushService.broadcastToRole(restaurantCode, Role.ADMIN.name(), TABLE_AVAILABILITY_EVENT, diningTables);
    }

    /**
     * A simple DTO to structure the notification content for the service worker.
     */
    @Getter
    private static class NotificationPayload {
        private final String title;
        private final String body;

        public NotificationPayload(String title, String body) {
            this.title = title;
            this.body = body;
        }
    }
}

package com.justintime.jit.service.impl;

import com.justintime.jit.entity.Enums.Role;
import com.justintime.jit.entity.OrderEntities.Order;
import com.justintime.jit.entity.OrderEntities.OrderItem;
import com.justintime.jit.service.NotificationService;
import com.justintime.jit.service.PushService;
import org.springframework.stereotype.Service;

import static com.justintime.jit.util.constants.JITConstants.ORDER_ITEM_CREATED_EVENT;
import static com.justintime.jit.util.constants.JITConstants.ORDER_ITEM_STATUS_UPDATED_EVENT;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final PushService pushService;

    public NotificationServiceImpl(PushService pushService) {
        this.pushService = pushService;
    }

    @Override
    public void notifyOrderItemCreation(Order order) {
        order.getOrderItems().forEach(this::notifyOrderItemCreationToUser);
    }

    @Override
    public void notifyOrderItemStatusUpdate(OrderItem orderItem) {
        notifyOrderItemStatusUpdateToUser(orderItem);
        broadcastOrderItemStatusUpdate(orderItem, Role.ADMIN.name());
    }

    private void notifyOrderItemCreationToUser(OrderItem orderItem) {
        // TODO get the restaurant code from the jwt bean
        String restaurantCode = orderItem.getOrder().getRestaurant().getRestaurantCode();
        String email = orderItem.getCook().getEmail();
        String role = Role.COOK.name();
        pushService.sendToUser(restaurantCode, email, role, ORDER_ITEM_CREATED_EVENT, orderItem);
    }

    private void notifyOrderItemStatusUpdateToUser(OrderItem orderItem) {
        String restaurantCode = orderItem.getOrder().getRestaurant().getRestaurantCode();
        String email = orderItem.getOrder().getUser().getEmail();
        String role = Role.SERVER.name();
        pushService.sendToUser(restaurantCode, email, role, ORDER_ITEM_STATUS_UPDATED_EVENT, orderItem);
    }

    private void broadcastOrderItemStatusUpdate(OrderItem orderItem, String role) {
        String restaurantCode = orderItem.getOrder().getRestaurant().getRestaurantCode();
        pushService.broadcastToRole(restaurantCode, role, ORDER_ITEM_STATUS_UPDATED_EVENT, orderItem);
    }
}

package com.justintime.jit.service.impl;

import com.justintime.jit.dto.OrderDTO;
import com.justintime.jit.dto.OrderItemDTO;
import com.justintime.jit.entity.DiningTable;
import com.justintime.jit.entity.Enums.Role;
import com.justintime.jit.entity.OrderEntities.Order;
import com.justintime.jit.entity.OrderEntities.OrderItem;
import com.justintime.jit.service.NotificationService;
import com.justintime.jit.service.PushService;
import com.justintime.jit.util.mapper.GenericMapper;
import com.justintime.jit.util.mapper.MapperFactory;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.justintime.jit.util.constants.JITConstants.*;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final PushService pushService;

    public NotificationServiceImpl(PushService pushService) {
        this.pushService = pushService;
    }

    private final GenericMapper<OrderItem, OrderItemDTO> orderItemMapper = MapperFactory.getMapper(OrderItem.class, OrderItemDTO.class);

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

    private void notifyOrderItemCreationToUser(OrderItem orderItem) {
        String email = orderItem.getCook().getEmail();
        OrderItemDTO orderItemDTO = orderItemMapper.toDto(orderItem);
        pushService.sendToUser(email, ORDER_ITEM_CREATED_EVENT ,orderItemDTO);
    }

    private void notifyOrderItemStatusUpdateToUser(OrderItem orderItem) {
        String email = orderItem.getOrder().getUser().getEmail();
        OrderItemDTO orderItemDTO = orderItemMapper.toDto(orderItem);
        pushService.sendToUser(email, ORDER_ITEM_STATUS_UPDATED_EVENT, orderItemDTO);
    }

    private void notifyOrderStatusUpdateToUser(OrderDTO order) {
        String email = order.getServerEmail();
//        OrderStatusUpdateDTO orderStatusUpdateDTO = new OrderStatusUpdateDTO(order.getOrderNumber(), order.getStatus());
        pushService.sendToUser(email, ORDER_ITEM_STATUS_UPDATED_EVENT, order);
    }

    private void broadcastOrderItemStatusUpdate(OrderItem orderItem, String role) {
        String restaurantCode = orderItem.getOrder().getRestaurant().getRestaurantCode();
        OrderItemDTO orderItemDTO = orderItemMapper.toDto(orderItem);
        pushService.broadcastToRole(restaurantCode, role, ORDER_ITEM_STATUS_UPDATED_EVENT, orderItemDTO);
    }

    private void broadcastTableAvailability(List<DiningTable> diningTables) {
        String restaurantCode = "";
        if(diningTables.stream().findFirst().isPresent()) {
            restaurantCode = diningTables.stream().findFirst().get().getRestaurant().getRestaurantCode();
        }
        if(StringUtils.isBlank(restaurantCode)) throw new RuntimeException("Issue at NotificationServiceImpl, Restaurant code is not found");
        pushService.broadcastToRole(restaurantCode, Role.SERVER.name(), TABLE_AVAILABILITY_EVENT, diningTables);
        pushService.broadcastToRole(restaurantCode, Role.ADMIN.name(), TABLE_AVAILABILITY_EVENT, diningTables);
    }
}

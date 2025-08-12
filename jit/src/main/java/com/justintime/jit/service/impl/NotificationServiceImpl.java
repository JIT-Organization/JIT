package com.justintime.jit.service.impl;

import com.justintime.jit.dto.OrderItemDTO;
import com.justintime.jit.entity.Enums.Role;
import com.justintime.jit.entity.OrderEntities.OrderItem;
import com.justintime.jit.service.NotificationService;
import com.justintime.jit.service.PushService;
import com.justintime.jit.util.constants.JITConstants;
import com.justintime.jit.util.mapper.GenericMapper;
import com.justintime.jit.util.mapper.MapperFactory;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.justintime.jit.util.constants.JITConstants.ORDER_ITEM_CREATED_EVENT;
import static com.justintime.jit.util.constants.JITConstants.ORDER_ITEM_STATUS_UPDATED_EVENT;

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

    @Override
    public void notifyOrderItemStatusUpdate(OrderItem orderItem) {
        notifyOrderItemStatusUpdateToUser(orderItem);
        broadcastOrderItemStatusUpdate(orderItem, Role.ADMIN.name());
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

    private void broadcastOrderItemStatusUpdate(OrderItem orderItem, String role) {
        String restaurantCode = orderItem.getOrder().getRestaurant().getRestaurantCode();
        OrderItemDTO orderItemDTO = orderItemMapper.toDto(orderItem);
        pushService.broadcastToRole(restaurantCode, role, ORDER_ITEM_STATUS_UPDATED_EVENT, orderItemDTO);
    }
}

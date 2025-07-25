package com.justintime.jit.service;

import com.justintime.jit.entity.OrderEntities.Order;
import com.justintime.jit.entity.OrderEntities.OrderItem;

import java.util.List;

public interface NotificationService {
    void notifyOrderItemCreation(List<OrderItem> orderItems);
    void notifyOrderItemStatusUpdate(OrderItem orderItem);
    // TODO Add for notification when order item is updated or cancelled
}

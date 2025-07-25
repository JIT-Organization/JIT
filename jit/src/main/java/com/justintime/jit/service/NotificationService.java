package com.justintime.jit.service;

import com.justintime.jit.entity.OrderEntities.Order;
import com.justintime.jit.entity.OrderEntities.OrderItem;

public interface NotificationService {
    void notifyOrderItemCreation(Order order);
    void notifyOrderItemStatusUpdate(OrderItem orderItem);
}

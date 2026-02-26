package com.justintime.jit.service;

import com.justintime.jit.dto.OrderDTO;
import com.justintime.jit.entity.DiningTable;
import com.justintime.jit.dto.SubscriptionRequest;
import com.justintime.jit.entity.OrderEntities.OrderItem;

import java.util.List;

public interface NotificationService {
    void notifyOrderItemCreation(List<OrderItem> orderItems);

    void notifyOrderItemStatusUpdate(OrderItem orderItem);

    void notifyTableAvailabilityUpdate(List<DiningTable> diningTables);

    void notifyOrderStatusUpdate(OrderDTO order);

    // TODO Add for notification when order item is updated or cancelled
    void subscribePushNotifications(SubscriptionRequest subscriptionDetails);
}

package com.justintime.jit.event.listeners;

import com.justintime.jit.event.OrderCreatedEvent;
import com.justintime.jit.event.OrderStatusUpdateEvent;
import com.justintime.jit.service.NotificationService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class OrderCreatedEventListener {
    private final NotificationService notificationService;

    public OrderCreatedEventListener(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @TransactionalEventListener
    public void listenOrderCreationEvent(OrderCreatedEvent event) {
        notificationService.notifyOrderItemCreation(event.getOrderItems());
    }

    @TransactionalEventListener
    public void listenOrderStatusUpdateEvent(OrderStatusUpdateEvent orderStatusUpdateEvent) {
        notificationService.notifyOrderStatusUpdate(orderStatusUpdateEvent.getOrder());
    }
}

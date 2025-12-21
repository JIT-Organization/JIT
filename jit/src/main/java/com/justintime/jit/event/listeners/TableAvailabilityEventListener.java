package com.justintime.jit.event.listeners;

import com.justintime.jit.event.OrderCreatedEvent;
import com.justintime.jit.event.TableAvailabilityEvent;
import com.justintime.jit.service.NotificationService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class TableAvailabilityEventListener {
    private final NotificationService notificationService;

    public TableAvailabilityEventListener(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @TransactionalEventListener
    public void listenTableAvailabilityEvent(TableAvailabilityEvent event) {
        notificationService.notifyTableAvailabilityUpdate(event.getDiningTables());
    }
}

package com.justintime.jit.event;

import com.justintime.jit.entity.OrderEntities.OrderItem;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.List;

@Getter
public class OrderCreatedEvent extends ApplicationEvent {
    private final List<OrderItem> orderItems;
    public OrderCreatedEvent(Object source, List<OrderItem> orderItems) {
        super(source);
        this.orderItems = orderItems;
    }
}

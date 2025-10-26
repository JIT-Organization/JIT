package com.justintime.jit.event;

import com.justintime.jit.dto.OrderDTO;
import com.justintime.jit.entity.OrderEntities.Order;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class OrderStatusUpdateEvent extends ApplicationEvent {
    private final OrderDTO order;

    public OrderStatusUpdateEvent(Object source, OrderDTO order) {
        super(source);
        this.order = order;
    }
}

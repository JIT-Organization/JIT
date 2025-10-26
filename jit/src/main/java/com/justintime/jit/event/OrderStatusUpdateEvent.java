package com.justintime.jit.event;

import com.justintime.jit.dto.OrderDTO;
import com.justintime.jit.entity.OrderEntities.Order;
import lombok.Getter;
import org.springframework.beans.BeanUtils;
import org.springframework.context.ApplicationEvent;

@Getter
public class OrderStatusUpdateEvent extends ApplicationEvent {
    private final OrderDTO order;

    private OrderStatusUpdateEvent(Object source, OrderDTO safeCopy) {
        super(source);
        this.order = safeCopy;
    }

    public static OrderStatusUpdateEvent of(Object source, OrderDTO original) {
        OrderDTO copy = new OrderDTO();
        BeanUtils.copyProperties(original, copy);
        return new OrderStatusUpdateEvent(source, copy);
    }
}

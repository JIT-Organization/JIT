package com.justintime.jit.dto;

import com.justintime.jit.entity.Enums.OrderStatus;

public record OrderStatusUpdateDTO(String orderNumber, OrderStatus status) {
}

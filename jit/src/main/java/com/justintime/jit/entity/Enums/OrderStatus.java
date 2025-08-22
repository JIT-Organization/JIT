package com.justintime.jit.entity.Enums;

public enum OrderStatus {
    NEW,
    PREPARING, // At least one order item started
    SERVING, // At least one order item ready_to_serve
    SERVED, // All order item served
    COMPLETED, // Payment done
    CANCELLED // Can cancel order only when it is new
}

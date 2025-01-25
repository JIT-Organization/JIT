package com.justintime.jit.service;

import com.justintime.jit.entity.Enums.OrderStatus;
import com.justintime.jit.entity.OrderEntities.Order;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface OrderService {
    Order createOrder(Order order);
    List<Order> getAllOrders();
    Order getOrderById(Long id);
    Order updateOrderStatus(Long id, OrderStatus status);
    void deleteOrder(Long id);
    List<Order> getOrdersByRestaurantAndCustomerId(Optional<Long> restaurantId, Optional<Long> customerId);
    BigDecimal calculateTotalRevenue(Long restaurantId);
}

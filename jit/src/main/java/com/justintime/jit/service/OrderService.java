package com.justintime.jit.service;

import com.justintime.jit.dto.OrderDTO;
import com.justintime.jit.entity.Enums.OrderStatus;
import com.justintime.jit.entity.OrderEntities.Order;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface OrderService {
    ResponseEntity<String> createOrder(Long restaurantId, Long userId, OrderDTO orderDTO);
    List<OrderDTO> getOrdersByRestaurantId(Long restaurantId);
    OrderDTO getOrderByRestaurantAndId(Long restaurantId, Long id);
    OrderDTO updateOrderStatus(Long restaurantId, Long id, OrderStatus status);
    void deleteOrder(Long restaurantId, Long id);
    List<OrderDTO> getOrdersByRestaurantAndUserId(Optional<Long> restaurantId, Optional<Long> userId);
    BigDecimal calculateTotalRevenue(Long restaurantId);
}

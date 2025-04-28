package com.justintime.jit.service;

import com.justintime.jit.dto.OrderDTO;
import com.justintime.jit.entity.Enums.OrderStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

public interface OrderService {
    ResponseEntity<String> createOrder(String restaurantCode, String username, OrderDTO orderDTO);
    List<OrderDTO> getOrdersByRestaurantId(String restaurantCode);
    OrderDTO getOrderByRestaurantAndId(String restaurantCode, Long id);
    OrderDTO updateOrderStatus(String restaurantCode, Long id, OrderStatus status);
    OrderDTO patchUpdateOrder(String restaurantCode, Long orderId, OrderDTO orderDTO, HashSet<String> propertiesToBeUpdated);
    void deleteOrder(String restaurantCode, Long id);
    List<OrderDTO> getOrdersByRestaurantAndUserId(Optional<Long> restaurantId, Optional<Long> userId);
    BigDecimal calculateTotalRevenue(String restaurantCode);
}

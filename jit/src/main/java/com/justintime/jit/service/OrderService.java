package com.justintime.jit.service;

import com.justintime.jit.dto.OrderDTO;
import com.justintime.jit.entity.Enums.OrderStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

public interface OrderService {
    ResponseEntity<String> createOrder(OrderDTO orderDTO);
    List<OrderDTO> getOrdersByRestaurantId();
    OrderDTO getOrderByRestaurantAndOrderNumber(String restaurantCode, String orderNumber);
    OrderDTO updateOrderStatus(String restaurantCode, String orderNumber, OrderStatus status);
    OrderDTO patchUpdateOrder(String restaurantCode, String orderNumber, OrderDTO orderDTO, HashSet<String> propertiesToBeUpdated);
    void deleteOrder(String restaurantCode, String orderNumber);
    List<OrderDTO> getOrdersByRestaurantAndUserId(Optional<Long> restaurantId, Optional<Long> userId);
    BigDecimal calculateTotalRevenue(String restaurantCode);
}

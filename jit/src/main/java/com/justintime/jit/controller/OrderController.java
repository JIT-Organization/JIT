package com.justintime.jit.controller;

import com.justintime.jit.dto.OrderDTO;
import com.justintime.jit.dto.PatchRequest;
import com.justintime.jit.entity.Enums.OrderStatus;
import com.justintime.jit.entity.OrderEntities.Order;
import com.justintime.jit.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/jit-api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/{restaurantId}/{userId}")
    public ResponseEntity<String> createOrder(@PathVariable Long restaurantId, @PathVariable Long userId, @RequestBody OrderDTO orderDTO) {
        return orderService.createOrder(restaurantId, userId, orderDTO);
    }

    @GetMapping("/{restaurantId}")
    public ResponseEntity<List<OrderDTO>> getAllOrders(@PathVariable Long restaurantId) {
        List<OrderDTO> orderDTOs = orderService.getOrdersByRestaurantId(restaurantId);
        return ResponseEntity.ok(orderDTOs);
    }

    @GetMapping("/{restaurantId}/{orderId}")
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable Long restaurantId, @PathVariable Long orderId) {
        OrderDTO orderDTO = orderService.getOrderByRestaurantAndId(restaurantId, orderId);
        return ResponseEntity.ok(orderDTO);
    }

    @PutMapping("/{restaurantId}/{orderId}/status")
    public ResponseEntity<OrderDTO> updateOrderStatus(
            @PathVariable Long restaurantId,
            @PathVariable Long orderId,
            @RequestParam OrderStatus status) {
        OrderDTO updatedOrderDTO = orderService.updateOrderStatus(restaurantId, orderId, status);
        return ResponseEntity.ok(updatedOrderDTO);
    }

    @PatchMapping("/{restaurantId}/{orderId}")
    public OrderDTO patchUpdateOrder(
            @PathVariable Long restaurantId,
            @PathVariable Long orderId,
            @RequestBody PatchRequest<OrderDTO> payload){
        return orderService.patchUpdateOrder(restaurantId, orderId, payload.getDto(), payload.getPropertiesToBeUpdated());
    }

    @DeleteMapping("/{restaurantId}/{orderId}")
    public ResponseEntity<String> deleteOrder(@PathVariable Long restaurantId, @PathVariable Long orderId) {
        orderService.deleteOrder(restaurantId,orderId);
        return ResponseEntity.ok("Order deleted successfully.");
    }

    @GetMapping("/{restaurantId}/users/{userId}/orders")
    public ResponseEntity<List<OrderDTO>> getOrdersByRestaurantAndUserId(
            @PathVariable(required = false) Long restaurantId,
            @PathVariable(required = false) Long userId) {

        Optional<Long> optRestaurantId = Optional.ofNullable(restaurantId);
        Optional<Long> optUserId = Optional.ofNullable(userId);

        List<OrderDTO> orderDTOs = orderService.getOrdersByRestaurantAndUserId(optRestaurantId, optUserId);
        return ResponseEntity.ok(orderDTOs);
    }

    @GetMapping("/{restaurantId}/revenue")
    public ResponseEntity<BigDecimal> calculateTotalRevenue(@PathVariable Long restaurantId) {
        BigDecimal totalRevenue = orderService.calculateTotalRevenue(restaurantId);
        return ResponseEntity.ok(totalRevenue); }
}

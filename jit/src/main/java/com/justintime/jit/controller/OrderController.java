package com.justintime.jit.controller;

import com.justintime.jit.entity.Enums.OrderStatus;
import com.justintime.jit.entity.OrderEntities.Order;
import com.justintime.jit.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/jit-api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    // Create a new order
    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody Order order) {
        Order createdOrder = orderService.createOrder(order);
        return ResponseEntity.ok(createdOrder);
    }

    // Get all orders
    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        List<Order> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }

    // Get an order by ID
    @GetMapping("/{orderId}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long orderId) {
        Order order = orderService.getOrderById(orderId);
        return ResponseEntity.ok(order);
    }

    // Update order status
    @PutMapping("/{orderId}/status")
    public ResponseEntity<Order> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestParam OrderStatus status) {
        Order updatedOrder = orderService.updateOrderStatus(orderId, status);
        return ResponseEntity.ok(updatedOrder);
    }

    // Delete an order
    @DeleteMapping("/{orderId}")
    public ResponseEntity<String> deleteOrder(@PathVariable Long orderId) {
        orderService.deleteOrder(orderId);
        return ResponseEntity.ok("Order deleted successfully.");
    }

    // Get orders by restaurant ID and customer ID
    @GetMapping("/{restaurantId}/customers/{customerId}/orders")
    public ResponseEntity<List<Order>> getOrdersByRestaurantAndCustomerId(
            @PathVariable(required = false) Long restaurantId,
            @PathVariable(required = false) Long customerId) {

        // Convert to Optional before passing to service
        Optional<Long> optRestaurantId = Optional.ofNullable(restaurantId);
        Optional<Long> optCustomerId = Optional.ofNullable(customerId);

        List<Order> orders = orderService.getOrdersByRestaurantAndCustomerId(optRestaurantId, optCustomerId);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{restaurantId}/revenue")
    public ResponseEntity<BigDecimal> calculateTotalRevenue(@PathVariable Long restaurantId) {
        BigDecimal totalRevenue = orderService.calculateTotalRevenue(restaurantId);
        return ResponseEntity.ok(totalRevenue); }
}

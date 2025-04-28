package com.justintime.jit.controller;

import com.justintime.jit.dto.ApiResponse;
import com.justintime.jit.dto.OrderDTO;
import com.justintime.jit.dto.PatchRequest;
import com.justintime.jit.entity.Enums.OrderStatus;
import com.justintime.jit.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/jit-api/orders")
public class OrderController extends BaseController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/{restaurantCode}/{username}")
    public ResponseEntity<String> createOrder(@PathVariable String restaurantCode, @PathVariable String username, @RequestBody OrderDTO orderDTO) {
        return orderService.createOrder(restaurantCode, username, orderDTO);
    }

    @GetMapping("/{restaurantCode}")
    public ResponseEntity<ApiResponse<List<OrderDTO>>> getAllOrders(@PathVariable String restaurantCode) {
        List<OrderDTO> orderDTOs = orderService.getOrdersByRestaurantId(restaurantCode);
        return success(orderDTOs, "Orders fetched successfully");
    }

    @GetMapping("/{restaurantCode}/{orderId}")
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable String restaurantCode, @PathVariable Long orderId) {
        OrderDTO orderDTO = orderService.getOrderByRestaurantAndId(restaurantCode, orderId);
        return ResponseEntity.ok(orderDTO);
    }

    @PutMapping("/{restaurantCode}/{orderId}/status")
    public ResponseEntity<OrderDTO> updateOrderStatus(
            @PathVariable String restaurantCode,
            @PathVariable Long orderId,
            @RequestParam OrderStatus status) {
        OrderDTO updatedOrderDTO = orderService.updateOrderStatus(restaurantCode, orderId, status);
        return ResponseEntity.ok(updatedOrderDTO);
    }

    @PatchMapping("/{restaurantCode}/{orderId}")
    public OrderDTO patchUpdateOrder(
            @PathVariable String restaurantCode,
            @PathVariable Long orderId,
            @RequestBody PatchRequest<OrderDTO> payload){
        return orderService.patchUpdateOrder(restaurantCode, orderId, payload.getDto(), payload.getPropertiesToBeUpdated());
    }

    @DeleteMapping("/{restaurantCode}/{orderId}")
    public ResponseEntity<String> deleteOrder(@PathVariable String restaurantCode, @PathVariable Long orderId) {
        orderService.deleteOrder(restaurantCode,orderId);
        return ResponseEntity.ok("Order deleted successfully.");
    }

    @GetMapping("/{restaurantCode}/users/{username}/orders")
    public ResponseEntity<List<OrderDTO>> getOrdersByRestaurantAndUserId(
            @PathVariable(required = false) Long restaurantId,
            @PathVariable(required = false) Long userId) {

        Optional<Long> optRestaurantId = Optional.ofNullable(restaurantId);
        Optional<Long> optUserId = Optional.ofNullable(userId);

        List<OrderDTO> orderDTOs = orderService.getOrdersByRestaurantAndUserId(optRestaurantId, optUserId);
        return ResponseEntity.ok(orderDTOs);
    }

    @GetMapping("/{restaurantCode}/revenue")
    public ResponseEntity<BigDecimal> calculateTotalRevenue(@PathVariable String restaurantCode) {
        BigDecimal totalRevenue = orderService.calculateTotalRevenue(restaurantCode);
        return ResponseEntity.ok(totalRevenue); }
}

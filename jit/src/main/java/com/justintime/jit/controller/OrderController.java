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

    @PostMapping("/{restaurantCode}")
    public ResponseEntity<String> createOrder(@PathVariable String restaurantCode, @RequestBody OrderDTO orderDTO) {
        return orderService.createOrder(restaurantCode, orderDTO);
    }

    @GetMapping("/{restaurantCode}")
    public ResponseEntity<ApiResponse<List<OrderDTO>>> getAllOrders(@PathVariable String restaurantCode) {
        List<OrderDTO> orderDTOs = orderService.getOrdersByRestaurantId(restaurantCode);
        return success(orderDTOs, "Orders fetched successfully");
    }

    @GetMapping("/{restaurantCode}/{orderNumber}")
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable String restaurantCode, @PathVariable String orderNumber) {
        OrderDTO orderDTO = orderService.getOrderByRestaurantAndOrderNumber(restaurantCode, orderNumber);
        return ResponseEntity.ok(orderDTO);
    }

    @PutMapping("/{restaurantCode}/{orderNumber}/status")
    public ResponseEntity<OrderDTO> updateOrderStatus(
            @PathVariable String restaurantCode,
            @PathVariable String orderNumber,
            @RequestParam OrderStatus status) {
        OrderDTO updatedOrderDTO = orderService.updateOrderStatus(restaurantCode, orderNumber, status);
        return ResponseEntity.ok(updatedOrderDTO);
    }

    @PatchMapping("/{restaurantCode}/{orderNumber}")
    public OrderDTO patchUpdateOrder(
            @PathVariable String restaurantCode,
            @PathVariable String orderNumber,
            @RequestBody PatchRequest<OrderDTO> payload){
        return orderService.patchUpdateOrder(restaurantCode, orderNumber, payload.getDto(), payload.getPropertiesToBeUpdated());
    }

    @DeleteMapping("/{restaurantCode}/{orderNumber}")
    public ResponseEntity<String> deleteOrder(@PathVariable String restaurantCode, @PathVariable String orderNumber) {
        orderService.deleteOrder(restaurantCode, orderNumber);
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

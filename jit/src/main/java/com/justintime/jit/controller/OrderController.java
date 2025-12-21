package com.justintime.jit.controller;

import com.justintime.jit.dto.ApiResponse;
import com.justintime.jit.dto.OrderDTO;
import com.justintime.jit.dto.OrderItemDTO;
import com.justintime.jit.dto.PatchRequest;
import com.justintime.jit.entity.Enums.OrderStatus;
import com.justintime.jit.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/orders")
public class OrderController extends BaseController {

    @Autowired
    private OrderService orderService;

    @PostMapping
    @PreAuthorize("hasPermission(null, 'ADD_ORDERS')")
    public ResponseEntity<String> createOrder(@RequestBody OrderDTO orderDTO) {
        return orderService.createOrder(orderDTO);
    }

    @GetMapping
    @PreAuthorize("hasPermission(null, 'VIEW_ORDERS')")
    public ResponseEntity<ApiResponse<List<OrderDTO>>> getAllOrders() {
        List<OrderDTO> orderDTOs = orderService.getOrdersByRestaurantId();
        return success(orderDTOs, "Orders fetched successfully");
    }

    @GetMapping("/{restaurantCode}/{orderNumber}")
    @PreAuthorize("hasPermission(null, 'VIEW_ORDERS')")
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable String restaurantCode, @PathVariable String orderNumber) {
        OrderDTO orderDTO = orderService.getOrderByRestaurantAndOrderNumber(restaurantCode, orderNumber);
        return ResponseEntity.ok(orderDTO);
    }

    @PutMapping("/{restaurantCode}/{orderNumber}/status")
    //    @PreAuthorize("hasPermission(null, 'ADD_ORDERS')")
    public ResponseEntity<OrderDTO> updateOrderStatus(
            @PathVariable String restaurantCode,
            @PathVariable String orderNumber,
            @RequestParam OrderStatus status) {
        OrderDTO updatedOrderDTO = orderService.updateOrderStatus(restaurantCode, orderNumber, status);
        return ResponseEntity.ok(updatedOrderDTO);
    }

    @PatchMapping("/{restaurantCode}/{orderNumber}")
    @PreAuthorize("hasPermission(null, 'ADD_ORDERS')")
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
    @PreAuthorize("hasPermission(null, 'VIEW_ORDERS')")
    public ResponseEntity<List<OrderDTO>> getOrdersByRestaurantAndUserId(
            @PathVariable(required = false) Long restaurantId,
            @PathVariable(required = false) Long userId) {

        Optional<Long> optRestaurantId = Optional.ofNullable(restaurantId);
        Optional<Long> optUserId = Optional.ofNullable(userId);

        List<OrderDTO> orderDTOs = orderService.getOrdersByRestaurantAndUserId(optRestaurantId, optUserId);
        return ResponseEntity.ok(orderDTOs);
    }

    @GetMapping("/{restaurantCode}/revenue")
    @PreAuthorize("hasPermission(null, 'ADD_ORDERS')")
    public ResponseEntity<BigDecimal> calculateTotalRevenue(@PathVariable String restaurantCode) {
        BigDecimal totalRevenue = orderService.calculateTotalRevenue(restaurantCode);
        return ResponseEntity.ok(totalRevenue);
    }

    @GetMapping("/orderItems")
    @PreAuthorize("hasPermission(null, 'VIEW_ORDERS')")
    public ResponseEntity<ApiResponse<List<OrderItemDTO>>> getAllInProgressOrderItemsForRestaurant() {
        List<OrderItemDTO> orderItemDTOS = orderService.getAllInProgressOrderItemsForRestaurant();
        return success(orderItemDTOS, "Orders fetched successfully");
    }

    @PatchMapping("/orderItem/updateStatus")
    @PreAuthorize("hasPermission(null, 'ADD_ORDERS')")
    public ResponseEntity<ApiResponse<OrderItemDTO>> updateOrderItemStatus(@RequestBody OrderItemDTO orderItemDTO) {
        OrderItemDTO returnOrderItemDTO = orderService.updateOrderItemStatus(orderItemDTO);
        return success(returnOrderItemDTO, "Order Item updated successfully");
    }
}

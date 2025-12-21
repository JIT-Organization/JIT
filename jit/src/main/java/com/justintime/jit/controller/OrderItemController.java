package com.justintime.jit.controller;

import com.justintime.jit.dto.MenuItemDTO;
import com.justintime.jit.dto.OrderItemDTO;
import com.justintime.jit.dto.PatchRequest;
import com.justintime.jit.entity.OrderEntities.OrderItem;
import com.justintime.jit.service.OrderItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/jit-api/OrderItems")
public class OrderItemController {

    @Autowired
    private OrderItemService orderItemService;

    @GetMapping
    public List<OrderItemDTO> getAllOrderItems() {
        return orderItemService.getAllOrderItems();
    }

    @GetMapping("/{restaurantCode}/{orderNumber}")
    public List<OrderItemDTO> getOrderItemsByRestaurantCodeAndOrderNumber(@PathVariable String restaurantCode,@PathVariable String orderNumber) {
        return orderItemService.getOrderItemsByRestaurantCodeAndOrderNumber(restaurantCode,orderNumber);
    }

    @PutMapping("/{restaurantCode}")
    public OrderItemDTO updateOrderItem(@PathVariable String restaurantCode, @RequestBody OrderItemDTO orderItemDTO) {
        return orderItemService.updateOrderItem(restaurantCode, orderItemDTO);
    }

    @PatchMapping("/{restaurantCode}")
    public OrderItemDTO patchOrderItem(@PathVariable String restaurantCode,  @RequestBody PatchRequest<OrderItemDTO> payload) {
        return orderItemService.patchOrderItem(restaurantCode, payload);
    }

    @PostMapping("/{restaurantCode}")
    public void createOrderItem(@PathVariable String restaurantCode,@RequestBody OrderItemDTO orderItemDTO) {
        orderItemService.saveOrderItem(restaurantCode,orderItemDTO);
    }

    @DeleteMapping("/{restaurantCode}/{itemName}/{orderNumber}")
    public void deleteOrderItem(@PathVariable String restaurantCode, @PathVariable String itemName, @PathVariable String orderNumber) {
        orderItemService.deleteOrderItem(restaurantCode, itemName, orderNumber);
    }

    @GetMapping("/{restaurantCode}/{cookName}")
    public ResponseEntity<List<OrderItemDTO>> getOrderItemsForCookByNameAndRestaurant(@PathVariable String cookName, @PathVariable String restaurantCode) {
        List<OrderItemDTO> orderItems = orderItemService.getOrderItemsForCookByNameAndRestaurant(cookName, restaurantCode);
        return ResponseEntity.ok(orderItems);
    }
}

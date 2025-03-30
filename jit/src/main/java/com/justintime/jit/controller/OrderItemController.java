package com.justintime.jit.controller;


import com.justintime.jit.entity.OrderEntities.OrderItem;
import com.justintime.jit.service.OrderItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/jit-api/OrderItems")
public class OrderItemController {


        @Autowired
        private OrderItemService orderItemService;

        @GetMapping
        public List<OrderItem> getAllOrderItems() {
            return orderItemService.getAllOrderItems();
        }

        @GetMapping("/{id}")
        public OrderItem getOrderItemById(@PathVariable Long id) {
            return orderItemService.getOrderItemById(id);
        }

        @PostMapping
        public OrderItem createOrderItem(@RequestBody OrderItem orderItem) {
            return orderItemService.saveOrderItem(orderItem);
        }

        @DeleteMapping("/{id}")
        public void deleteOrderItem(@PathVariable Long id) {
            orderItemService.deleteOrderItem(id);
        }

}

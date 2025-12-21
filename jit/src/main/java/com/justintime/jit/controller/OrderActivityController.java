package com.justintime.jit.controller;

import com.justintime.jit.entity.OrderEntities.OrderActivity;
import com.justintime.jit.service.OrderActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orderActivities")
public class OrderActivityController {
    @Autowired
    private OrderActivityService orderActivityService;

    @GetMapping
    public List<OrderActivity> getAllOrderActivities() {
        return orderActivityService.getAllOrderActivities();
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderActivity> getOrderActivityById(@PathVariable Long id) {
        return orderActivityService.getOrderActivityById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public OrderActivity createOrderActivity(@RequestBody OrderActivity orderActivity) {
        return orderActivityService.createOrderActivity(orderActivity);
    }

//    @PutMapping("/{id}")
//    public ResponseEntity<OrderActivity> updateOrderActivity(@PathVariable Long id, @RequestBody OrderActivity orderActivityDetails) {
//        try {
//            OrderActivity updatedOrderActivity = orderActivityService.updateOrderActivity(id, orderActivityDetails);
//            return ResponseEntity.ok(updatedOrderActivity);
//        } catch (RuntimeException e) {
//            return ResponseEntity.notFound().build();
//        }
//    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrderActivity(@PathVariable Long id) {
        orderActivityService.deleteOrderActivity(id);
        return ResponseEntity.noContent().build();
    }
}

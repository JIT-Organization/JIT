package com.justintime.jit.repository.OrderRepo;

import com.justintime.jit.entity.OrderEntities.Order;
import com.justintime.jit.repository.BaseRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.time.LocalDateTime;

@Repository
public interface OrderRepository extends BaseRepository<Order, Long> {

    // Find orders by customer ID
    List<Order> findByCustomerId(Long customerId);

    // Find all orders for a specific restaurant
    List<Order> findByRestaurantId(Long restaurantId);

    List<Order> findAll();


    // Find orders by status
    List<Order> findByStatus(String status);

    // Get orders within a time range
    List<Order> findByOrderDateBetween(LocalDateTime startTime, LocalDateTime endTime);

    List<Order> findByRestaurantIdAndCustomerId(Long restaurantId, Long customerId);
}

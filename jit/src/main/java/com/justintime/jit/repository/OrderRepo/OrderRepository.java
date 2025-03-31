package com.justintime.jit.repository.OrderRepo;

import com.justintime.jit.entity.OrderEntities.Order;
import com.justintime.jit.repository.BaseRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface OrderRepository extends BaseRepository<Order, Long> {

    // Find orders by customer ID
    List<Order> findByUserId(Long userId);

    // Find all orders for a specific restaurant
    List<Order> findByRestaurantId(Long restaurantId);

    Optional<Order> findByRestaurantIdAndId(Long restaurantId, Long Id);

    List<Order> findAll();

    // Find orders by status
    List<Order> findByStatus(String status);

    // Get orders within a time range
    List<Order> findByOrderDateBetween(LocalDateTime startTime, LocalDateTime endTime);

    List<Order> findByRestaurantIdAndUserId(Long restaurantId, Long userId);
}

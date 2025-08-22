package com.justintime.jit.repository.OrderRepo;

import com.justintime.jit.entity.OrderEntities.Order;
import com.justintime.jit.repository.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface OrderRepository extends BaseRepository<Order, Long> {

    // Find orders by customer ID
    List<Order> findByUserId(Long userId);

    // Find all orders for a specific restaurant
    @Query(value = "SELECT o.* FROM orders o " +
            "JOIN restaurant r ON r.id = o.restaurant_id " +
            "WHERE r.restaurant_code = :resCode ",nativeQuery = true)
    List<Order> findByRestaurantCode(@Param("resCode") String restaurantCode);

    Optional<Order> findByRestaurantIdAndId(Long restaurantId, Long Id);

    @Query(value = "SELECT o.* FROM orders o " +
            "JOIN restaurant r ON r.id = o.restaurant_id " +
            "WHERE r.restaurant_code = :resCode AND o.order_number = :orderNumber",
            nativeQuery = true)
    Optional<Order> findByRestaurantCodeAndOrderNumber(
            @Param("resCode") String restaurantCode,
            @Param("orderNumber") String orderNumber
    );

    List<Order> findAll();

    // Find orders by status
    List<Order> findByStatus(String status);

    // Get orders within a time range
    List<Order> findByOrderDateBetween(LocalDateTime startTime, LocalDateTime endTime);

    List<Order> findByRestaurantIdAndUserId(Long restaurantId, Long userId);

    @Query(value = "SELECT o.* FROM orders o " +
            "JOIN restaurant r ON r.id = o.restaurant_id " +
            "WHERE r.restaurant_code = :resCode AND o.id = :id ", nativeQuery = true)
    Order findByRestaurantCodeAndId(@Param("resCode") String restaurantCode,@Param("id") Long id);

    List<Order> findByRestaurantId(Long restaurantId);

    Optional<Order> findByOrderNumber(String orderNumber);

    long countByRestaurantId(Long restaurantId);

    @Query(value = "SELECT SUM(o.amount) FROM orders o " +
            "WHERE o.restaurant_id = :restaurantId", nativeQuery = true)
    double calculateTotalRevenueByRestaurantId(Long restaurantId);
}

package com.justintime.jit.repository.OrderRepo;

import com.justintime.jit.entity.OrderEntities.OrderItem;
import com.justintime.jit.repository.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderItemRepository extends BaseRepository<OrderItem,Long> {
    @Query("SELECT oi.menuItem.food, COUNT(oi) " +
            "FROM OrderItem oi " +
            "WHERE oi.createdDttm >= :startDate " +
            "AND oi.menuItem.restaurant.id = :restaurantId " +
            "GROUP BY oi.menuItem.food " +
            "ORDER BY COUNT(oi) DESC")
    List<Object[]> findMenuItemsWithOrderCount(@Param("startDate") LocalDateTime startDate,
                                               @Param("restaurantId") Long restaurantId);
}

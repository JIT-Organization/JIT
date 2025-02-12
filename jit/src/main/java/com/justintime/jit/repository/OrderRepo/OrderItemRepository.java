package com.justintime.jit.repository.OrderRepo;

import com.justintime.jit.entity.MenuItem;
import com.justintime.jit.entity.OrderEntities.OrderItem;
import com.justintime.jit.repository.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.List;

@Repository
public interface OrderItemRepository extends BaseRepository<OrderItem,Long> {
    @Query("SELECT oi.menuItem, COUNT(oi) AS orderCount " +
            "FROM OrderItem oi " +
            "WHERE oi.menuItem.restaurant.id = :restaurantId " +
            "AND oi.menuItem.id IN :menuItemIds " +
            "GROUP BY oi.menuItem " +
            "ORDER BY orderCount DESC")
    List<Object[]> findMenuItemsWithOrderCount(@Param("restaurantId") Long restaurantId, @Param("menuItemIds") List<Long> menuItemIds);
//    List<Object[]> findMenuItemsWithOrderCount(@Param("startDate") LocalDateTime startDate,
//                                               @Param("addressId") Long addressId);
}

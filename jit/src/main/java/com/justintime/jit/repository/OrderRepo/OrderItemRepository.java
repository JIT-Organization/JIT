package com.justintime.jit.repository.OrderRepo;

import com.justintime.jit.entity.MenuItem;
import com.justintime.jit.entity.OrderEntities.OrderItem;
import com.justintime.jit.entity.BatchConfig;
import com.justintime.jit.repository.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
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

    @Query("SELECT oi.combo, COUNT(oi) AS orderCount " +
            "FROM OrderItem oi " +
            "WHERE oi.combo.restaurant.id = :restaurantId " +
            "AND oi.combo.id IN :comboIds " +
            "GROUP BY oi.combo " +
            "ORDER BY orderCount DESC")
    List<Object[]> findCombosWithOrderCount(@Param("restaurantId") Long restaurantId, @Param("comboIds") List<Long> itemIds);
//    List<Object[]> findMenuItemsWithOrderCount(@Param("startDate") LocalDateTime startDate,
//                                               @Param("addressId") Long addressId);

    @Query("SELECT oi FROM OrderItem oi WHERE oi.orderItemStatus = 'PENDING'")
    List<OrderItem> findAllPending();
    
    @Query("SELECT oi FROM OrderItem oi JOIN oi.batchOrderItems boi WHERE boi.batch.id = :batchId")
    List<OrderItem> findByBatchId(@Param("batchId") Long batchId);

    @Query("SELECT DISTINCT oi FROM OrderItem oi LEFT JOIN oi.batchOrderItems boi WHERE boi IS NULL")
    List<OrderItem> findUnassignedOrderItems();

    @Query("SELECT DISTINCT oi FROM OrderItem oi " +
           "LEFT JOIN oi.batchOrderItems boi " +
           "JOIN oi.menuItem mi " +
           "JOIN mi.batchConfig bc " +
           "WHERE boi IS NULL " +
           "AND bc IN :batchConfigs " +
           "AND NOT EXISTS (" +
           "    SELECT b FROM Batch b " +
           "    WHERE b.batchConfig = bc " +
           "    AND b.status = :status) " +
           "AND (oi.maxTimeLimitToStart IS NULL OR oi.maxTimeLimitToStart >= :currentTime) " +
           "AND EXISTS (" +
           "    SELECT 1 FROM BatchConfig bc2 " +
           "    JOIN bc2.cooks c " +
           "    WHERE bc2 = bc " +
           "    AND c.name = :cookName)")
    List<OrderItem> findUnassignedOrderItemsByBatchConfigAndStatus(
            @Param("batchConfigs") List<BatchConfig> batchConfigs,
            @Param("status") String status,
            @Param("cookName") String cookName,
            @Param("currentTime") LocalDateTime currentTime);

    @Query("SELECT COUNT(b) FROM Batch b " +
           "WHERE b.batchConfig = :batchConfig " +
           "AND b.status = :status " +
           "AND b.cook.name = :cookName")
    Long countAssignedBatchesForCook(
            @Param("batchConfig") BatchConfig batchConfig,
            @Param("status") String status,
            @Param("cookName") String cookName);

    @Query("SELECT COUNT(b) FROM Batch b " +
           "WHERE b.batchConfig = :batchConfig " +
           "AND b.status = :status")
    Long countUnassignedBatchesForBatchConfig(
            @Param("batchConfig") BatchConfig batchConfig,
            @Param("status") String status);
}

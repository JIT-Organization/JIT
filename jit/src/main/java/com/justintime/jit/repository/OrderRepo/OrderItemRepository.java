package com.justintime.jit.repository.OrderRepo;

import com.justintime.jit.entity.Enums.OrderItemStatus;
import com.justintime.jit.entity.OrderEntities.OrderItem;
import com.justintime.jit.entity.BatchConfig;
import com.justintime.jit.repository.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderItemRepository extends BaseRepository<OrderItem,Long> {
    @Query(value = "SELECT oi.menu_item_id, COUNT(oi.id) AS order_count " +
            "FROM order_item oi " +
            "JOIN restaurant r ON oi.restaurant_id = r.id " +
            "WHERE r.restaurant_code = :restaurantCode " +
            "AND oi.menu_item_id IN (:menuItemIds) " +
            "GROUP BY oi.menu_item_id " +
            "ORDER BY order_count DESC",
            nativeQuery = true)
    List<Object[]> findMenuItemsWithOrderCount(
            @Param("restaurantCode") String restaurantCode,
            @Param("menuItemIds") List<Long> menuItemIds);

    @Query(value = "SELECT oi.combo_id, COUNT(oi.id) AS order_count " +
            "FROM order_item oi " +
            "JOIN combo c ON oi.combo_id = c.id " +
            "JOIN restaurant r ON oi.restaurant_id = r.id " +
            "WHERE r.restaurant_code = :restaurantCode " +
            "AND c.id IN (:comboIds) " +
            "GROUP BY oi.combo_id " +
            "ORDER BY order_count DESC",
            nativeQuery = true)
    List<Object[]> findCombosWithOrderCount(
            @Param("restaurantCode") String restaurantCode,
            @Param("comboIds") List<Long> comboIds);
    @Query(value="SELECT oi FROM OrderItem oi join restaurant r on "+
            "oi.restaurant_id=r.id join order o on" +
            " oi.order_id=o.id " +
            "where r.restaurant_code = :restaurantCode "+
            "AND o.order_number=:orderNumber",nativeQuery = true)
    List<OrderItem> findByRestaurantCodeAndOrderNumber(@Param("restaurantCode") String restaurantCode,@Param("orderNumber") String orderNumber);


    @Query("SELECT oi FROM OrderItem oi WHERE oi.orderItemStatus = 'PENDING'")
    List<OrderItem> findAllPending();
    
    @Query("SELECT oi FROM OrderItem oi JOIN oi.batchOrderItems boi WHERE boi.batch.id = :batchId")
    List<OrderItem> findByBatchId(@Param("batchId") Long batchId);

    @Query("SELECT DISTINCT oi FROM OrderItem oi LEFT JOIN oi.batchOrderItems boi WHERE boi IS NULL")
    List<OrderItem> findUnassignedOrderItems();

    @Query("SELECT oi FROM OrderItem oi " +
           "WHERE oi.menuItem IS NOT NULL " +
           "AND oi.menuItem.batchConfig IN :batchConfigs " +
           "AND oi.orderItemStatus = :status " +
           "AND oi.batchOrderItems IS EMPTY " +
           "AND :cookId IN (SELECT c.id FROM oi.menuItem.batchConfig.cooks c) " +
           "AND oi.createdDttm <= :currentTime " +
           "AND oi.menuItem.restaurant.id = :restaurantId")
    List<OrderItem> findUnassignedOrderItemsByBatchConfigAndStatusAndRestaurantId(
        @Param("batchConfigs") List<BatchConfig> batchConfigs,
        @Param("status") String status,
        @Param("cookId") Long cookId,
        @Param("currentTime") LocalDateTime currentTime,
        @Param("restaurantId") Long restaurantId
    );

    @Query("SELECT COUNT(b) FROM Batch b " +
           "WHERE b.batchConfig = :batchConfig " +
           "AND b.status = :status " +
           "AND b.cook.id = :cookId " +
           "AND b.batchConfig.restaurant.id = :restaurantId")
    Long countAssignedBatchesForCookAndRestaurant(
        @Param("batchConfig") BatchConfig batchConfig,
        @Param("status") String status,
        @Param("cookId") Long cookId,
        @Param("restaurantId") Long restaurantId
    );

    @Query(value="SELECT oi.* FROM order_item oi " +
            "JOIN orders o ON oi.order_id = o.id " +
            "JOIN restaurant r ON o.restaurant_id = r.id " +
            "JOIN menu_item mi ON oi.menu_item_id = mi.id " +
            "WHERE r.restaurant_code = :restaurantCode " +
            "AND o.order_number = :orderNumber " +
            "AND mi.menu_item_name = :itemName",
            nativeQuery = true)
     OrderItem findByRestaurantCodeAndItemNameAndOrderNumber( @Param("restaurantCode") String restaurantCode, @Param("itemName") String itemName,@Param("orderNumber") String orderNumber);


      void deleteByOrderIdAndMenuItemId(
              Long orderId,
              Long menuItemId);

//    @Query("SELECT oi FROM OrderItem oi WHERE (oi.assignedCookId = :cookId) OR (oi.assignedCookId IS NULL AND (oi.order.orderDate IS NULL OR oi.order.orderDate - oi.preparationTime <= :now))")
//    List<OrderItem> findOrderItemsForCook(@Param("cookId") Long cookId, @Param("now") LocalDateTime now);

    @Query("SELECT oi FROM OrderItem oi " +
           "WHERE (oi.cook.id = :cookId AND oi.orderItemStatus = 'ASSIGNED') " +
           "OR (oi.cook IS NULL AND " +
           "    ((oi.menuItem IS NOT NULL AND :cookId IN (SELECT c.id FROM oi.menuItem.cookSet c)) " +
           "     OR (oi.menuItem IS NOT NULL AND oi.menuItem.batchConfig IS NOT NULL AND :cookId IN (SELECT c.id FROM oi.menuItem.batchConfig.cooks c)))) " +
           "ORDER BY oi.createdDttm ASC")
    List<OrderItem> findOrderItemsByCookIdAndUnassigned(@Param("cookId") Long cookId);

    @Query("SELECT oi FROM OrderItem oi " +
            "WHERE oi.cook.id = :cookId " +
            "  AND (oi.orderItemStatus = 'ASSIGNED' OR oi.orderItemStatus = 'STARTED') " +
            "  AND oi.order.restaurant.restaurantCode = :restaurantCode " +
            "ORDER BY oi.createdDttm ASC")
    List<OrderItem> findAssignedAndStartedItemsByRestaurantCodeAndCook(@Param("restaurantCode") String restaurantCode, @Param("cookId") Long cookId);

    @Query("SELECT oi FROM OrderItem oi " +
            "WHERE (oi.orderItemStatus = 'ASSIGNED' OR oi.orderItemStatus = 'STARTED') " +
            "  AND oi.order.restaurant.restaurantCode = :restaurantCode " +
            "ORDER BY oi.createdDttm ASC")
    List<OrderItem> findAssignedAndStartedItemsByRestaurantCode(@Param("restaurantCode") String restaurantCode);

    List<OrderItem> findByOrder_Restaurant_RestaurantCodeAndOrderItemStatusIn(String restaurantCode, List<OrderItemStatus> orderItemStatuses);
}

package com.justintime.jit.repository;

import com.justintime.jit.entity.Batch;
import com.justintime.jit.entity.Enums.BatchStatus;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface BatchRepository extends BaseRepository<Batch, Long> {
    
    @Query(value = "SELECT b.* FROM batch b " +
           "JOIN menu_item mi ON mi.batch_config_id = b.batch_config_id " +
           "WHERE mi.id = :menuItemId " +
           "AND b.restaurant_id = :restaurantId " +
           "AND b.status = :status", nativeQuery = true)
    Set<Batch> findByMenuItemIdAndRestaurantIdAndStatus(
            @Param("menuItemId") Long menuItemId,
            @Param("restaurantId") Long restaurantId,
            @Param("status") BatchStatus status);
            
    @Query(value = "SELECT b.* FROM batch b " +
           "JOIN cook c ON b.cook_id = c.id " +
           "WHERE c.name = :cookName " +
           "AND b.status = :status", nativeQuery = true)
    List<Batch> findByCookNameAndStatus(@Param("cookName") String cookName, @Param("status") String status);

    @Query(value = "SELECT b.* FROM batch b " +
           "JOIN cook c ON b.cook_id = c.id " +
           "JOIN batch_config bc ON b.batch_config_id = bc.id " +
           "WHERE c.name = :cookName " +
           "AND b.status = :status " +
           "AND (:batchConfigNumber IS NULL OR bc.batch_number = :batchConfigNumber)", nativeQuery = true)
    List<Batch> findByCookNameAndStatusAndBatchConfigNumber(
            @Param("cookName") String cookName, 
            @Param("status") String status,
            @Param("batchConfigNumber") String batchConfigNumber);

    @Query("SELECT b FROM Batch b " +
           "JOIN b.batchConfig bc " +
           "JOIN bc.menuItems mi " +
           "WHERE mi.id = :menuItemId " +
           "AND b.restaurant.id = :restaurantId")
    Set<Batch> findByMenuItemIdAndRestaurantId(
            @Param("menuItemId") Long menuItemId,
            @Param("restaurantId") Long restaurantId);
}

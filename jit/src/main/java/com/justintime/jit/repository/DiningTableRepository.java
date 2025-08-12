package com.justintime.jit.repository;

import com.justintime.jit.dto.DiningTableDTO;
import com.justintime.jit.entity.DiningTable;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiningTableRepository extends BaseRepository<DiningTable, Long> {
    List<DiningTableDTO> findByRestaurantId(Long restaurantId);

    @Query(value = "SELECT dt.* from dining_table dt " +
            "JOIN restaurant r ON dt.restaurant_id = r.id " +
            "WHERE r.restaurant_code = :restaurantCode", nativeQuery = true)
    List<DiningTable> findByRestaurantCode(@Param("restaurantCode") String restaurantCode);

    @Query(value = "SELECT dt.* from dining_table dt " +
            "JOIN restaurant r ON dt.restaurant_id = r.id " +
            "WHERE r.restaurant_code = :restaurantCode AND dt.table_number = :tableNumber", nativeQuery = true)
    DiningTable findByRestaurantCodeAndTableNumber(@Param("restaurantCode") String restaurantCode, @Param("tableNumber") String tableNumber);

    @Modifying
    @Query(value = "DELETE dt FROM dining_table dt " +
            "JOIN restaurant r ON dt.restaurant_id = r.id " +
            "WHERE r.restaurant_code = :restaurantCode AND dt.table_number = :tableNumber", nativeQuery = true)
    void deleteTableByRestaurantCodeAndTableNumber(@Param("restaurantCode") String restaurantCode, @Param("tableNumber") String tableNumber);

    long countByRestaurantId(Long restaurantId);
}

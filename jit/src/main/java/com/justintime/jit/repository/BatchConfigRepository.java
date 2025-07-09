package com.justintime.jit.repository;

import com.justintime.jit.entity.BatchConfig;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BatchConfigRepository extends BaseRepository<BatchConfig, Long> {

    @Query("SELECT COUNT(b) FROM Batch b " +
            "WHERE b.batchConfig = :batchConfig " +
            "AND b.status = :status " +
            "AND b.batchConfig.restaurant.id = :restaurantId")
    Long countUnassignedBatchesForBatchConfigAndRestaurant(
            @Param("batchConfig") BatchConfig batchConfig,
            @Param("status") String status,
            @Param("restaurantId") Long restaurantId
    );
}

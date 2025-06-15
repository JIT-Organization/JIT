package com.justintime.jit.repository;


import com.justintime.jit.entity.BatchConfig;
import com.justintime.jit.entity.OrderEntities.OrderItem;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BatchConfigRepository extends BaseRepository<BatchConfig,Long> {
    void deleteByRestaurantIdAndBatchConfigNumber(Long restaurantId,String batchConfigNumber);

    @Query(value = "SELECT b FROM Batch b WHERE b.batchConfig.batchConfigNumber = :batchConfigNumber AND b.batchConfig.restaurant.id = :id",nativeQuery = true)
    BatchConfig findBatchConfigByBatchConfigNumberAndRestaurantId(String batchConfigNumber, Long id);

    List<OrderItem> findOrderItemsByBatchId(Long id);
}

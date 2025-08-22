package com.justintime.jit.service;

import com.justintime.jit.dto.BatchDTO;
import com.justintime.jit.dto.CategoryDTO;
import com.justintime.jit.entity.Batch;
import com.justintime.jit.entity.Category;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

public interface BatchService extends BaseService<Batch, Long> {
//    List<BatchDTO> getAllBatches(String restaurantCode);
//    Batch createBatch(Long restaurantId, BatchDTO batchDTO);
//    Optional<BatchDTO> getBatchByRestaurantIdAndId(Long restaurantId, Long id);
    List<BatchDTO> getBatchesByRestaurantCodeAndCookName(String restaurantCode, String cookName);
//    Batch updateBatch(Long restaurantId,Long id, BatchDTO updatedBatchDTO);
//    Batch patchUpdateBatch(Long restaurantId, Long id, BatchDTO batchDTO, HashSet<String> propertiesToBeUpdated);
//    void deleteBatch(Long id);
}

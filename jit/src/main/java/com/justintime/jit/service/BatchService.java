package com.justintime.jit.service;

import com.justintime.jit.dto.BatchDTO;
import com.justintime.jit.dto.CategoryDTO;
import com.justintime.jit.entity.Batch;
import com.justintime.jit.entity.Category;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

public interface BatchService extends BaseService<Batch, Long> {
    List<Batch> getAllBatches(String restaurantCode);
    Batch addBatch(String restaurantCode, BatchDTO batchDTO);
    List<BatchDTO> getBatchesByRestaurantCodeAndCookName(String restaurantCode, String cookName);
    Batch updateBatch(String restaurantCode,Long id, BatchDTO updatedBatchDTO);
    Batch patchUpdateBatch(String restaurantCode, Long id, BatchDTO batchDTO, HashSet<String> propertiesToBeUpdated);
    void deleteBatch(String restaurantCode, String batchName);
}

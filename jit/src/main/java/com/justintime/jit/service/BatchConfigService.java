package com.justintime.jit.service;


import com.justintime.jit.dto.BatchConfigDTO;
import com.justintime.jit.entity.BatchConfig;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

public interface BatchConfigService extends BaseService<BatchConfig,Long> {
    List<BatchConfigDTO> getAllBatchConfigs(String restaurantCode);
    BatchConfig addBatchConfig(String restaurantCode, BatchConfigDTO batchConfigDTO);
    Optional<BatchConfigDTO> getBatchConfigByRestaurantCodeAndId(String restaurantCode, Long id);
    BatchConfig updateBatchConfig(String restaurantCode,Long id, BatchConfigDTO updatedBatchConfigDTO);
    BatchConfig patchUpdateBatchConfig(String restaurantCode, Long id, BatchConfigDTO batchConfigDTO, HashSet<String> propertiesToBeUpdated);
    void deleteBatchConfig(String restaurantCode, String batchName);
}

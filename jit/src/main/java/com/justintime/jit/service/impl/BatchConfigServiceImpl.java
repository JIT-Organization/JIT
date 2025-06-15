package com.justintime.jit.service.impl;

import com.justintime.jit.dto.BatchConfigDTO;
import com.justintime.jit.dto.BatchDTO;
import com.justintime.jit.entity.Batch;
import com.justintime.jit.entity.BatchConfig;
import com.justintime.jit.entity.TimeInterval;
import com.justintime.jit.exception.ResourceNotFoundException;
import com.justintime.jit.repository.BatchConfigRepository;
import com.justintime.jit.repository.RestaurantRepository;
import com.justintime.jit.service.BatchConfigService;
import com.justintime.jit.util.mapper.GenericMapper;
import com.justintime.jit.util.mapper.MapperFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class BatchConfigServiceImpl extends BaseServiceImpl<Batch, Long> implements BatchConfigService {

    @Autowired
    private BatchConfigService batchConfigService;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private BatchConfigRepository batchConfigRepository;

    @Override
    public List<BatchConfigDTO> getAllBatchConfigs(String restaurantCode) {

        return List.of();
    }

    @Override
    public BatchConfig addBatchConfigs(String restaurantCode, BatchConfigDTO batchConfigDTO) {
        GenericMapper<BatchConfig, BatchConfigDTO> mapper = MapperFactory.getMapper(BatchConfig.class, BatchConfigDTO.class);
        BatchConfig batchConfig = mapper.toEntity(batchConfigDTO);
        batchConfig.setRestaurant(restaurantRepository.findByRestaurantCode(restaurantCode).orElseThrow(() -> new RuntimeException("Restaurant not found")));
        resolveRelationships(batchConfig,batchConfigDTO);
        batchConfig.setUpdatedDttm(LocalDateTime.now());
        return batchConfigService.save(batchConfig);
    }

    @Override
    public Optional<BatchConfigDTO> getBatchByRestaurantCodeAndId(String restaurantCode, Long id) {

    }

    @Override
    public BatchConfig updateBatchConfigs(String restaurantCode, Long id, BatchConfigDTO updatedBatchConfigDTO) {

    }

    @Override
    public BatchConfig patchUpdateBatchConfigs(String restaurantCode, Long id, BatchConfigDTO batchDTO, HashSet<String> propertiesToBeUpdated) {

    }

    @Override
    public void deleteBatchConfigs(String restaurantCode, String batchConfigNumber) {
        Long restaurantId= restaurantRepository.findByRestaurantCode(restaurantCode)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found")).getId();
        batchConfigRepository.deleteByRestaurantIdAndBatchConfigNumber(restaurantId, batchConfigNumber);
    }

    private void resolveRelationships(BatchConfig batchConfig, BatchConfigDTO batchConfigDTO) {
        if (batchDTO.getBatchNumber() != null) {
            Set<Batch> categories = batchRepository.findByBatchNamesAndRestaurantId(
                    menuItemDTO.getCategorySet(), menuItem.getRestaurant().getId());
            menuItem.setCategorySet(categories);
        }
        if (menuItemDTO.getTimeIntervalSet() != null) {
            menuItem.setTimeIntervalSet(menuItemDTO.getTimeIntervalSet().stream()
                    .map(dto -> timeIntervalRepository.findByStartTimeAndEndTime(dto.getStartTime(), dto.getEndTime())
                            .orElseGet(() -> {
                                TimeInterval newInterval = new TimeInterval();
                                newInterval.setStartTime(dto.getStartTime());
                                newInterval.setEndTime(dto.getEndTime());
                                return timeIntervalRepository.save(newInterval);
                            }))
                    .collect(Collectors.toSet()));
        }
    }

    private BatchConfig mapToDTO(BatchConfig batchConfig, GenericMapper<BatchConfig, BatchConfigDTO> batchMapper) {
    }

    @Override
    public BatchConfig save(BatchConfig entity) {
        return null;
    }
}

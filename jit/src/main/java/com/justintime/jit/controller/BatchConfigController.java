package com.justintime.jit.controller;

import com.justintime.jit.dto.ApiResponse;
import com.justintime.jit.dto.BatchConfigDTO;
import com.justintime.jit.dto.BatchDTO;
import com.justintime.jit.dto.PatchRequest;
import com.justintime.jit.entity.BatchConfig;
import com.justintime.jit.service.BatchConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

public class BatchConfigController extends BaseController{
    @Autowired
    private BatchConfigService batchConfigService;

    @GetMapping("/getAll/{restaurantCode}")
    public ResponseEntity<ApiResponse<List<BatchConfigDTO>>> getAllBatchConfigs(@PathVariable String restaurantCode) {
        return success(batchConfigService.getAllBatchConfigs(restaurantCode));
    }

    @GetMapping("/{restaurantCode}/{id}")
    public ResponseEntity<ApiResponse<BatchConfigDTO>> getBatchByRestaurantCodeAndId(@PathVariable String restaurantCode, @PathVariable Long id) {
        Optional<BatchConfigDTO> batchConfigDTO = batchConfigService.getBatchByRestaurantCodeAndId(restaurantCode, id);
        if(batchConfigDTO.isPresent()) {
            return success(batchConfigDTO.get());
        }
        return error("BatchConfig doesn't exist", HttpStatus.NOT_FOUND);
    }

    @PostMapping("/{restaurantCode}/{id}/")
    public ResponseEntity<ApiResponse<BatchConfig>> addBatchConfig(@PathVariable String restaurantCode, @RequestBody BatchConfigDTO batchConfigDTO) {
        return success(batchConfigService.addBatchConfig(restaurantCode,batchConfigDTO), "BatchConfig Created Successfully");
    }

    @PutMapping("{restaurantCode}/{id}")
    public ResponseEntity<BatchConfig> updateBatchConfig(@PathVariable String restaurantCode,@PathVariable Long id, @RequestBody BatchConfigDTO batchConfigDTO) {
        return ResponseEntity.ok(batchConfigService.updateBatchConfig(restaurantCode,id, batchConfigDTO));
    }

    @PatchMapping("{restaurantCode}/{id}")
    public ResponseEntity<ApiResponse<BatchConfig>> patchUpdateBatchConfig(@PathVariable String restaurantCode,@PathVariable Long id, @RequestBody PatchRequest<BatchConfigDTO> payload) {
        return success(batchConfigService.patchUpdateBatchConfig(restaurantCode,id, payload.getDto(), payload.getPropertiesToBeUpdated()));
    }

    @DeleteMapping("/{restaurantCode}/{batchNumber}")
    public ResponseEntity<String> deleteBatchConfig(@PathVariable String restaurantCode,@PathVariable String batchNumber) {
        batchConfigService.deleteBatchConfig(restaurantCode,batchNumber);
        return ResponseEntity.ok("BatchConfig deleted successfully");
    }
}

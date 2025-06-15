package com.justintime.jit.controller;

import com.justintime.jit.dto.ApiResponse;
import com.justintime.jit.dto.BatchDTO;
import com.justintime.jit.dto.PatchRequest;
import com.justintime.jit.entity.Batch;
import com.justintime.jit.service.BatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

public class BatchController extends BaseController{
    @Autowired
    private BatchService batchService;

    @GetMapping("/getAll/{restaurantCode}")
    public ResponseEntity<ApiResponse<List<BatchDTO>>> getAllBatches(@PathVariable String restaurantCode) {
        return success(batchService.getAllBatches(restaurantCode));
    }

    @GetMapping("/{restaurantCode}/{id}")
    public ResponseEntity<ApiResponse<BatchDTO>> getBatchByRestaurantCodeAndId(@PathVariable String restaurantCode, @PathVariable Long id) {
        Optional<BatchDTO> batchDTO = batchService.getBatchByRestaurantCodeAndId(restaurantCode, id);
        if(batchDTO.isPresent()) {
            return success(batchDTO.get());
        }
        return error("Batch doesn't exist", HttpStatus.NOT_FOUND);
    }

    @PostMapping("/{restaurantCode}/{id}/")
    public ResponseEntity<ApiResponse<Batch>> addBatch(@PathVariable String restaurantCode, @RequestBody BatchDTO batchDTO) {
        return success(batchService.addBatch(restaurantCode,batchDTO), "Batch Created Successfully");
    }

    @PutMapping("{restaurantCode}/{id}")
    public ResponseEntity<Batch> updateBatch(@PathVariable String restaurantCode,@PathVariable Long id, @RequestBody BatchDTO batchDTO) {
        return ResponseEntity.ok(batchService.updateBatch(restaurantCode,id, batchDTO));
    }

    @PatchMapping("{restaurantCode}/{id}")
    public ResponseEntity<ApiResponse<Batch>> patchUpdateBatch(@PathVariable String restaurantCode,@PathVariable Long id, @RequestBody PatchRequest<BatchDTO> payload) {
        return success(batchService.patchUpdateBatch(restaurantCode,id, payload.getDto(), payload.getPropertiesToBeUpdated()));
    }

    @DeleteMapping("/{restaurantCode}/{batchName}")
    public ResponseEntity<String> deleteBatch(@PathVariable String restaurantCode,@PathVariable String batchName) {
        batchService.deleteBatch(restaurantCode,batchName);
        return ResponseEntity.ok("Batch deleted successfully");
    }
}

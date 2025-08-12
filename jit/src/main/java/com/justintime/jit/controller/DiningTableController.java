package com.justintime.jit.controller;

import com.justintime.jit.dto.ApiResponse;
import com.justintime.jit.dto.DiningTableDTO;
import com.justintime.jit.dto.PatchRequest;
import com.justintime.jit.service.DiningTableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/jit-api/tables")
public class DiningTableController extends BaseController {
    @Autowired
    private DiningTableService diningTableService;

    @GetMapping("/{restaurantCode}")
    public ResponseEntity<ApiResponse<List<DiningTableDTO>>> getTablesByRestaurant(@PathVariable String restaurantCode) {
        List<DiningTableDTO> tables = diningTableService.getDiningTablesByRestaurantCode(restaurantCode);
        return success(tables, "Fetched the tables successfully");
    }

    @PatchMapping("/{restaurantCode}")
    public ResponseEntity<ApiResponse<DiningTableDTO>> patchUpdateDiningTable(@PathVariable String restaurantCode, @RequestBody PatchRequest<DiningTableDTO> payload) {
        DiningTableDTO table = diningTableService.patchUpdateTablesByRestaurantCode(restaurantCode, payload.getDto(), payload.getPropertiesToBeUpdated());
        return success(table, "Updated Successfully");
    }

    @PostMapping("/{restaurantCode}")
    public ResponseEntity<ApiResponse<DiningTableDTO>> createTable(@PathVariable String restaurantCode, @RequestBody DiningTableDTO payload) {
        DiningTableDTO table = diningTableService.createTable(restaurantCode, payload);
        return success(table);
    }

    @DeleteMapping("/{restaurantCode}/{tableNumber}")
    public ResponseEntity<ApiResponse<DiningTableDTO>> deleteTable(@PathVariable String restaurantCode, @PathVariable String tableNumber) {
        diningTableService.deleteTable(restaurantCode, tableNumber);
        return success(null, "Table Deleted Successfully");
    }
}

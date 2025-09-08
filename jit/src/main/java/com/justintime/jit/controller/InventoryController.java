package com.justintime.jit.controller;

import com.justintime.jit.dto.ApiResponse;
import com.justintime.jit.dto.CategoryDTO;
import com.justintime.jit.dto.InventoryDTO;
import com.justintime.jit.dto.PatchRequest;
import com.justintime.jit.entity.Category;
import com.justintime.jit.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/jit-api/inventories")
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;

    @PostMapping("/{restaurantCode}")
    public ResponseEntity<ApiResponse<Category>> createInventory(@PathVariable String restaurantCode, @RequestBody InventoryDTO inventoryDTO) {
        return success(inventoryService.createInventory(restaurantCode, inventoryDTO), "Category Created Successfully");
    }

    @GetMapping("/getAll/{restaurantCode}")
    public ResponseEntity<ApiResponse<List<CategoryDTO>>> getAllInventories(@PathVariable String restaurantCode) {
        return success(inventoryService.getAllInventories(restaurantCode));
    }

    @GetMapping("/{restaurantCode}/{itemName}")
    public ResponseEntity<InventoryDTO> getInventoryByItem(
            @PathVariable String restaurantCode,
            @PathVariable String itemName) {
        InventoryDTO inventoryDTO = inventoryService.getInventoryByRestaurantAndItemName(restaurantCode, itemName);
        return ResponseEntity.ok(inventoryDTO);
    }

    @PutMapping("/{restaurantCode}/{itemName}/status")
    public ResponseEntity<InventoryDTO> updateInventory(
            @PathVariable String restaurantCode,
            @PathVariable String itemName) {
        InventoryDTO updatedInventoryDTO = inventoryService.updateInventory(restaurantCode, itemName);
        return ResponseEntity.ok(updatedInventoryDTO);
    }

    @PatchMapping("/{restaurantCode}/{itemName}")
    public InventoryDTO patchUpdateInventory(
            @PathVariable String restaurantCode,
            @PathVariable String itemName,
            @RequestBody PatchRequest<InventoryDTO> payload) {
        return inventoryService.patchUpdateInventory(
                restaurantCode, itemName, payload.getDto(), payload.getPropertiesToBeUpdated());
    }

    @DeleteMapping("/{restaurantCode}/{itemName}")
    public ResponseEntity<String> deleteInventory(
            @PathVariable String restaurantCode,
            @PathVariable String itemName) {
        inventoryService.deleteInventory(restaurantCode, itemName);
        return ResponseEntity.ok("Inventory item deleted successfully.");
    }
}

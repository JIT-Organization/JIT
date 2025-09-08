package com.justintime.jit.service;

import com.justintime.jit.dto.InventoryDTO;
import com.justintime.jit.entity.Inventory;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;


public interface InventoryService extends BaseService<Inventory,Long>{
    List<InventoryDTO> getAllInventories(String restaurantCode);
    String createInventory(String restaurantCode,InventoryDTO inventoryDTO);
    Optional<InventoryDTO> get(Long restaurantId, Long id);
    InventoryDTO updateInventory(String restaurantCode, String categoryName, InventoryDTO updatedInventoryDTO);
    InventoryDTO patchUpdateInventory(String restaurantCode, String categoryName, InventoryDTO inventoryDTO, HashSet<String> propertiesToBeUpdated);
    void deleteInventory(String restaurantCode, String categoryName);
}

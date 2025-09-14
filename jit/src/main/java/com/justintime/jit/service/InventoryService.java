package com.justintime.jit.service;

import com.justintime.jit.dto.InventoryDTO;
import com.justintime.jit.entity.Inventory;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;


public interface InventoryService extends BaseService<Inventory,Long>{
    List<InventoryDTO> getAllInventories(String restaurantCode);
    InventoryDTO createInventory(String restaurantCode,InventoryDTO inventoryDTO);
    InventoryDTO getInventoryByRestaurantAndItemName(String restaurantCode, String itemName);
    InventoryDTO updateInventory(String restaurantCode, String ItemName, InventoryDTO updatedInventoryDTO);
    InventoryDTO patchUpdateInventory(String restaurantCode, String ItemName, InventoryDTO inventoryDTO, HashSet<String> propertiesToBeUpdated);
    void deleteInventory(String restaurantCode, String ItemName);
}

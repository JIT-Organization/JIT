package com.justintime.jit.service.impl;

import com.justintime.jit.dto.InventoryDTO;
import com.justintime.jit.entity.Inventory;
import com.justintime.jit.entity.User;
import com.justintime.jit.exception.ResourceNotFoundException;
import com.justintime.jit.repository.InventoryRepository;
import com.justintime.jit.repository.RestaurantRepository;
import com.justintime.jit.repository.UserRepository;
import com.justintime.jit.service.InventoryService;
import com.justintime.jit.util.CommonServiceImplUtil;
import com.justintime.jit.util.mapper.GenericMapper;
import com.justintime.jit.util.mapper.MapperFactory;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InventoryServiceImpl extends BaseServiceImpl<Inventory, Long> implements InventoryService {

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommonServiceImplUtil commonServiceImplUtil;

    private final GenericMapper<Inventory, InventoryDTO> mapper =
            MapperFactory.getMapper(Inventory.class, InventoryDTO.class);

    public List<InventoryDTO> getAllInventories(String restaurantCode) {
        List<Inventory> inventories = inventoryRepository.findByRestaurant_RestaurantCode(restaurantCode);
        return inventories.stream()
                .map(inv -> mapper.toDto(inv))
                .collect(Collectors.toList());
    }


    public InventoryDTO getInventoryByRestaurantAndItem(String restaurantCode, String itemName) {
        Inventory inventory = inventoryRepository
                .findByRestaurant_RestaurantCodeAndItemName(restaurantCode, itemName)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory item not found"));
        return mapper.toDto(inventory);
    }


    @Transactional
    public InventoryDTO createInventory(String restaurantCode, InventoryDTO inventoryDTO) {
        Inventory inventory = mapper.toEntity(inventoryDTO);
        inventory.setRestaurant(
                restaurantRepository.findByRestaurantCode(restaurantCode)
                        .orElseThrow(() -> new RuntimeException("Restaurant not found"))
        );

        // Resolve supplier if provided
        if (inventoryDTO.getSupplierId() != null) {
            User supplier = userRepository.findById(inventoryDTO.getSupplierId())
                    .orElseThrow(() -> new ResourceNotFoundException("Supplier not found"));
            inventory.setSupplier(supplier);
        }

        Inventory savedInventory = inventoryRepository.save(inventory);
        return mapper.toDto(savedInventory);
    }

    @Transactional
    public InventoryDTO updateInventory(String restaurantCode, String itemName, InventoryDTO updatedInventoryDTO) {
        Inventory existingInventory = inventoryRepository
                .findByRestaurant_RestaurantCodeAndItemName(restaurantCode, itemName)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory item not found"));

        Inventory updatedInventory = mapper.toEntity(updatedInventoryDTO);
        updatedInventory.setId(existingInventory.getId());
        updatedInventory.setRestaurant(existingInventory.getRestaurant());

        // Keep supplier relationship if not updated
        if (updatedInventoryDTO.getSupplierId() == null) {
            updatedInventory.setSupplier(existingInventory.getSupplier());
        }

        Inventory savedInventory = inventoryRepository.save(updatedInventory);
        return mapper.toDto(savedInventory);
    }


    @Transactional
    public InventoryDTO patchUpdateInventory(String restaurantCode, String itemName, InventoryDTO dto, HashSet<String> propertiesToBeUpdated) {
        Inventory existingInventory = inventoryRepository
                .findByRestaurant_RestaurantCodeAndItemName(restaurantCode, itemName)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory item not found"));

        Inventory patchedInventory = mapper.toEntity(dto);

        // Copy only selected fields
        commonServiceImplUtil.copySelectedProperties(patchedInventory, existingInventory, propertiesToBeUpdated);
        existingInventory.setUpdatedDttm(LocalDateTime.now());

        Inventory savedInventory = inventoryRepository.save(existingInventory);
        return mapper.toDto(savedInventory);
    }

    /**
     * Delete an inventory item
     */
    @Transactional
    public void deleteInventory(String restaurantCode, String itemName) {
        Inventory existingInventory = inventoryRepository
                .findByRestaurant_RestaurantCodeAndItemName(restaurantCode, itemName)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory item not found"));
        inventoryRepository.delete(existingInventory);
    }
}

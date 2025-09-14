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

    @Override
    public List<InventoryDTO> getAllInventories(String restaurantCode) {
        return inventoryRepository.findByRestaurant_RestaurantCode(restaurantCode)
                .stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public InventoryDTO getInventoryByRestaurantAndItemName(String restaurantCode, String itemName) {
        Inventory inventory = inventoryRepository
                .findByRestaurant_RestaurantCodeAndItemName(restaurantCode, itemName)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory item not found"));
        return mapper.toDto(inventory);
    }

    @Override
    @Transactional
    public InventoryDTO createInventory(String restaurantCode, InventoryDTO inventoryDTO) {
        Inventory inventory = mapper.toEntity(inventoryDTO);
        inventory.setRestaurant(
                restaurantRepository.findByRestaurantCode(restaurantCode)
                        .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found"))
        );

        if (inventoryDTO.getSupplierId() != null) {
            User supplier = userRepository.findById(inventoryDTO.getSupplierId())
                    .orElseThrow(() -> new ResourceNotFoundException("Supplier not found"));
            inventory.setSupplier(supplier);
        }

        return mapper.toDto(inventoryRepository.save(inventory));
    }

    @Override
    @Transactional
    public InventoryDTO updateInventory(String restaurantCode, String itemName, InventoryDTO updatedInventoryDTO) {
        Inventory existingInventory = inventoryRepository
                .findByRestaurant_RestaurantCodeAndItemName(restaurantCode, itemName)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory item not found"));

        Inventory updatedInventory = mapper.toEntity(updatedInventoryDTO);
        updatedInventory.setId(existingInventory.getId());
        updatedInventory.setRestaurant(existingInventory.getRestaurant());

        if (updatedInventoryDTO.getSupplierId() == null) {
            updatedInventory.setSupplier(existingInventory.getSupplier());
        }

        return mapper.toDto(inventoryRepository.save(updatedInventory));
    }

    @Override
    @Transactional
    public InventoryDTO patchUpdateInventory(String restaurantCode, String itemName, InventoryDTO dto, HashSet<String> propertiesToBeUpdated) {
        Inventory existingInventory = inventoryRepository
                .findByRestaurant_RestaurantCodeAndItemName(restaurantCode, itemName)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory item not found"));

        Inventory patchedInventory = mapper.toEntity(dto);
        commonServiceImplUtil.copySelectedProperties(patchedInventory, existingInventory, propertiesToBeUpdated);
        return mapper.toDto(inventoryRepository.save(existingInventory));
    }

    @Override
    @Transactional
    public void deleteInventory(String restaurantCode, String itemName) {
        Inventory existingInventory = inventoryRepository
                .findByRestaurant_RestaurantCodeAndItemName(restaurantCode, itemName)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory item not found"));
        inventoryRepository.delete(existingInventory);
    }
}

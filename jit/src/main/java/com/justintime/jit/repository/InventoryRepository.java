package com.justintime.jit.repository;

import com.justintime.jit.entity.Inventory;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepository extends BaseRepository<Inventory, Long>{

        List<Inventory> findByRestaurant_RestaurantCode(String restaurantCode);
        Optional<Inventory> findByRestaurant_RestaurantCodeAndItemName(String restaurantCode, String itemName);
        boolean existsByRestaurant_RestaurantCodeAndItemName(String restaurantCode, String itemName);
    }

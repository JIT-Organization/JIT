package com.justintime.jit.service;

import com.justintime.jit.dto.ItemDTO;
import com.justintime.jit.entity.Enums.FoodType;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public interface ItemService {
    List<ItemDTO> getAllItemsForRestaurant(String restaurantCode);
    List<String> getAllItemNamesForRestaurant(String restaurantCode);
    List<String> getAllItemNamesForRestaurantAndFoodType(String restaurantCode, FoodType foodType);
    List<ItemDTO> getAllItemsForRestaurantAndFoodType(String restaurantCode, FoodType foodType);
    ItemDTO getItemByRestaurantAndNameAndFoodType(String restaurantCode, String itemName, FoodType foodType);
    ItemDTO createItem(String restaurantCode, FoodType foodType, ItemDTO itemDTO);
    ItemDTO updateItem(String restaurantCode, String ItemName, FoodType foodType, ItemDTO itemDTO);
    ItemDTO patchItem(String restaurantCode, String ItemName, FoodType foodType, ItemDTO itemDTO, HashSet<String> propertiesToBeUpdated);
    void deleteItem(String restaurantCode, String itemName, FoodType foodType);
}

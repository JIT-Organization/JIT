package com.justintime.jit.service;

import com.justintime.jit.dto.ItemDTO;
import com.justintime.jit.entity.Enums.FoodType;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public interface ItemService {
    List<ItemDTO> getAllItemsForRestaurant();
    List<String> getAllItemNamesForRestaurant();
    List<String> getAllItemNamesForRestaurantAndFoodType(FoodType foodType);
    List<ItemDTO> getAllItemsForRestaurantAndFoodType(FoodType foodType);
    ItemDTO getItemByRestaurantAndNameAndFoodType(String itemName, FoodType foodType);
    ItemDTO createItem(FoodType foodType, ItemDTO itemDTO);
    ItemDTO updateItem(String ItemName, FoodType foodType, ItemDTO itemDTO);
    ItemDTO patchItem(String ItemName, FoodType foodType, ItemDTO itemDTO, HashSet<String> propertiesToBeUpdated);
    void deleteItem(String itemName, FoodType foodType);
}

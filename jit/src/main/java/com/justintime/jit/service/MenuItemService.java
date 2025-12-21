package com.justintime.jit.service;

import com.justintime.jit.dto.MenuItemDTO;
import com.justintime.jit.entity.Enums.Sort;
import com.justintime.jit.entity.MenuItem;

import java.util.HashSet;
import java.util.List;

public interface MenuItemService extends BaseService<MenuItem,Long>{

    List<MenuItemDTO> getAllMenuItems();
    List<MenuItemDTO> getMenuItemsByRestaurantId(String restaurantCode, Sort sortBy, String priceRange, String category, Boolean onlyVeg, Boolean onlyForCombos);
    MenuItemDTO getMenuItemByRestaurantIdAndMenuItemName(String restaurantCode, String menuItemName);
    MenuItem addMenuItem(String restaurantCode,MenuItemDTO menuItemDTO);
    MenuItem updateMenuItem(String restaurantCode,Long id, MenuItemDTO updatedItem);
    MenuItemDTO patchUpdateMenuItem(String restaurantCode,String menuItemName, MenuItemDTO updatedItem, HashSet<String> propertiesToBeUpdated);
    void deleteMenuItem(String restaurantCode, String menuItemName);
}

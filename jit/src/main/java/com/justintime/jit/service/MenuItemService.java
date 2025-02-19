package com.justintime.jit.service;

import com.justintime.jit.dto.MenuItemDTO;
import com.justintime.jit.entity.Enums.Sort;
import com.justintime.jit.entity.MenuItem;

import java.util.List;

public interface MenuItemService extends BaseService<MenuItem,Long>{

    List<MenuItemDTO> getAllMenuItems();
    List<MenuItemDTO> getMenuItemsByRestaurantId(Long addressId, Sort sortBy, String priceRange, String category, Boolean onlyVeg, Boolean onlyForCombos);
    MenuItemDTO addMenuItem(MenuItemDTO menuItemDTO);
    MenuItemDTO updateMenuItem(Long id, MenuItemDTO menuItemDTO);
    void deleteMenuItem(Long id);
}

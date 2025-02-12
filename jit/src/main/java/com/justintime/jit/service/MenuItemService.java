package com.justintime.jit.service;

import com.justintime.jit.dto.MenuItemDTO;
import com.justintime.jit.entity.Enums.Filter;
import com.justintime.jit.entity.MenuItem;

import java.util.List;

public interface MenuItemService extends BaseService<MenuItem,Long>{

    List<MenuItemDTO> getAllMenuItems();
    List<MenuItemDTO> getMenuItemsByRestaurantId(Long addressId, Filter sortBy, String priceRange, String category, boolean onlyForCombos);
    MenuItem addMenuItem(MenuItemDTO menuItemDTO);
    MenuItem updateMenuItem(Long id, MenuItem updatedItem);
    void deleteMenuItem(Long id);
}

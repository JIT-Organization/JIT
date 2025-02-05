package com.justintime.jit.service;

import com.justintime.jit.entity.MenuItem;

import java.util.List;

public interface MenuItemService extends BaseService<MenuItem,Long>{

    List<MenuItem> getAllMenuItems();
    List<MenuItem> getMenuItemsByAddressId(Long addressId, String sortBy, String priceRange, boolean onlyForCombos);
    MenuItem addMenuItem(MenuItem menuItem);
    MenuItem updateMenuItem(Long id, MenuItem updatedItem);
    void deleteMenuItem(Long id);
}

package com.justintime.jit.service;

import com.justintime.jit.dto.AddOnDTO;
import com.justintime.jit.dto.MenuItemDTO;
import com.justintime.jit.entity.AddOn;
import com.justintime.jit.entity.Enums.Sort;
import com.justintime.jit.entity.MenuItem;

import java.util.HashSet;
import java.util.List;

public interface AddOnService extends BaseService<AddOn, Long> {
    List<AddOnDTO> getAllAddOnsForRestaurant(String restaurantCode);
    List<AddOnDTO> getAllAddOnsForMenuItem(String restaurantCode, String menuItemName);
    List<AddOnDTO> getAllAddOnsForCombo(String restaurantCode, String comboName);
    List<AddOnDTO> getAllAddOnsForOrderItem(String restaurantCode, String orderNumber, String itemName);
    void createAddOn(String restaurantCode, AddOnDTO addOnDTO);
    AddOnDTO updateAddOn(String restaurantCode, AddOnDTO addOnDTO);
    AddOnDTO patchAddOn(String restaurantCode, AddOnDTO addOnDTO, HashSet<String> propertiesToBeUpdated);
    void deleteAddOn(String restaurantCode, String label);
}

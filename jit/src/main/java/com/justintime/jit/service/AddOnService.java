package com.justintime.jit.service;

import com.justintime.jit.dto.AddOnDTO;
import com.justintime.jit.entity.AddOn;

import java.util.HashSet;
import java.util.List;

public interface AddOnService extends BaseService<AddOn, Long> {
    List<AddOnDTO> getAllAddOnsForRestaurant();
    List<AddOnDTO> getAllAddOnsForMenuItem(String menuItemName);
    List<AddOnDTO> getAllAddOnsForCombo(String comboName);
    List<AddOnDTO> getAllAddOnsForOrderItem(String orderNumber, String itemName);
    void createAddOn(AddOnDTO addOnDTO);
    AddOnDTO updateAddOn(AddOnDTO addOnDTO);
    AddOnDTO patchAddOn(AddOnDTO addOnDTO, HashSet<String> propertiesToBeUpdated);
    void deleteAddOn(String label);
}

package com.justintime.jit.service;

import com.justintime.jit.dto.ComboDTO;
import com.justintime.jit.entity.ComboEntities.Combo;
import com.justintime.jit.entity.Enums.Sort;

import java.util.List;
import java.util.Optional;

public interface ComboService extends BaseService<Combo,Long>{

    List<ComboDTO> getAllCombos();
    Optional<Combo> getComboById(Long id);
    List<ComboDTO> getCombosByRestaurantId(Long restaurantId, Sort sortBy, String priceRange, String category, Boolean onlyVeg, Boolean onlyForCombos);
    Combo createCombo(Combo combo);
    Combo updateCombo(Long id, Combo updatedCombo);
    void deleteCombo(Long id);
}

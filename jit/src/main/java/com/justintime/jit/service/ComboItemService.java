package com.justintime.jit.service;

import com.justintime.jit.entity.ComboEntities.Combo;
import com.justintime.jit.entity.ComboEntities.ComboItem;

import java.util.List;
import java.util.Optional;

public interface ComboItemService extends BaseService<ComboItem,Long> {
    List<ComboItem> getAllComboItems();
    Optional<ComboItem> getComboItemsById(Long id);
    ComboItem createComboItems(ComboItem comboItem);
    ComboItem updateComboItems(Long id, ComboItem updatedComboItems);
    void deleteComboItems(Long id);
}

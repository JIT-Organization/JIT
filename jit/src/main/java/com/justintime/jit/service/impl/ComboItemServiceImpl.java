package com.justintime.jit.service.impl;


import com.justintime.jit.entity.Category;
import com.justintime.jit.entity.ComboEntities.Combo;
import com.justintime.jit.entity.ComboEntities.ComboItem;
import com.justintime.jit.repository.ComboRepo.ComboItemRepository;
import com.justintime.jit.repository.ComboRepo.ComboRepository;
import com.justintime.jit.service.ComboItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ComboItemServiceImpl extends BaseServiceImpl<ComboItem,Long> implements ComboItemService {

    @Autowired
    private ComboItemRepository comboItemRepository;

    public List<ComboItem> getAllComboItems() {
        return comboItemRepository.findAll();
    }


    public Optional<ComboItem> getComboItemsById(Long id) {
        return comboItemRepository.findById(id);
    }

    public ComboItem createComboItems(ComboItem comboItem) {
            return comboItemRepository.save(comboItem);
        }

    public ComboItem updateComboItems(Long id, ComboItem updatedComboItems) {
        return comboItemRepository.findById(id)
                .map(comboItem -> {
                    comboItem.setComboSet(updatedComboItems.getComboSet());
                    comboItem.setMenuItem(updatedComboItems.getMenuItem());
                    comboItem.setUpdatedDttm(updatedComboItems.getUpdatedDttm());
                    return comboItemRepository.save(comboItem);
                }).orElseThrow(() -> new RuntimeException("ComboItem not found with id: " + id));
    }


    public void deleteComboItems(Long id) {
        comboItemRepository.deleteById(id);
    }
}

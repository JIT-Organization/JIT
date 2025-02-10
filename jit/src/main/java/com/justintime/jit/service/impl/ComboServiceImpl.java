package com.justintime.jit.service.impl;

import com.justintime.jit.entity.ComboEntities.Combo;
import com.justintime.jit.repository.ComboRepo.ComboRepository;
import com.justintime.jit.service.ComboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ComboServiceImpl extends BaseServiceImpl<Combo,Long> implements ComboService {
    @Autowired
    private ComboRepository comboRepository;

    public List<Combo> getAllCombos() {
        return comboRepository.findAll();
    }

    public Optional<Combo> getComboById(Long id) {
        return comboRepository.findById(id);
    }

    public Combo createCombo(Combo combo) {
        return comboRepository.save(combo);
    }

    public Combo updateCombo(Long id, Combo updatedCombo) {
        return comboRepository.findById(id)
                .map(combo -> {
                    combo.setComboItemSet(updatedCombo.getComboItemSet());
                    combo.setPrice(updatedCombo.getPrice());
                    combo.setStock(updatedCombo.getStock());
                    combo.setUpdatedDttm(updatedCombo.getUpdatedDttm());
                    return comboRepository.save(combo);
                }).orElseThrow(() -> new RuntimeException("Combo not found with id: " + id));
    }

    public void deleteCombo(Long id) {
        comboRepository.deleteById(id);
    }


}




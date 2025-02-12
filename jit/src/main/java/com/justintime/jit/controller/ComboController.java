package com.justintime.jit.controller;


import com.justintime.jit.dto.ComboDTO;
import com.justintime.jit.entity.ComboEntities.Combo;
import com.justintime.jit.service.ComboService;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/combos")
public class ComboController {

    @Autowired
    private ComboService comboService;

    @GetMapping
    public List<ComboDTO> getAllCombos() {
        return comboService.getAllCombos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Combo> getComboById(@PathVariable Long id) {
        return comboService.getComboById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Combo createCombo(@RequestBody Combo combo) {
        return comboService.createCombo(combo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Combo> updateCombo(@PathVariable Long id, @RequestBody Combo updatedCombo) {
        try {
            return ResponseEntity.ok(comboService.updateCombo(id, updatedCombo));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCombo(@PathVariable Long id) {
        comboService.deleteCombo(id);
        return ResponseEntity.noContent().build();
    }
}


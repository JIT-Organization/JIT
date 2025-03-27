package com.justintime.jit.controller;


import com.justintime.jit.entity.ComboEntities.Combo;
import com.justintime.jit.entity.ComboEntities.ComboItem;
import com.justintime.jit.service.ComboItemService;
import com.justintime.jit.service.ComboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/jit-api/ComboItems")
public class ComboItemController {
    @Autowired
    private ComboItemService comboItemService;

    @GetMapping
    public List<ComboItem> getAllComboItems() {
        return comboItemService.getAllComboItems();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ComboItem> getComboItemsById(@PathVariable Long id) {
        return comboItemService.getComboItemsById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ComboItem createComboItems(@RequestBody ComboItem comboitem) {
        return comboItemService.createComboItems(comboitem);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ComboItem> updateComboItems(@PathVariable Long id, @RequestBody ComboItem updatedComboItems) {
        try {
            return ResponseEntity.ok(comboItemService.updateComboItems(id, updatedComboItems));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComboItems(@PathVariable Long id) {
        comboItemService.deleteComboItems(id);
        return ResponseEntity.noContent().build();
    }
}

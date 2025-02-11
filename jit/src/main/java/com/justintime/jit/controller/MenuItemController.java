package com.justintime.jit.controller;

import com.justintime.jit.dto.MenuItemDTO;
import com.justintime.jit.entity.Enums.Filter;
import com.justintime.jit.entity.MenuItem;
import com.justintime.jit.exception.ImageSizeLimitExceededException;
import com.justintime.jit.util.ImageValidation;
import com.justintime.jit.service.ComboItemService;
import com.justintime.jit.service.MenuItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/menu-items")
public class MenuItemController {

    @Autowired
    private MenuItemService menuItemService;

    @GetMapping
    public List<MenuItemDTO> getAllMenuItems() {
        return menuItemService.getAllMenuItems();
    }

    @GetMapping("/restaurant/{restaurantId}")
    public List<MenuItemDTO> getMenuItemsByRestaurant(
            @PathVariable Long restaurantId,
            @RequestParam(required = false) Filter sortBy,
            @RequestParam(required = false) String priceRange,
            @RequestParam(required = false) String category,
            @RequestParam(required = false, defaultValue = "false") boolean onlyForCombos) {
        return menuItemService.getMenuItemsByRestaurantId(restaurantId, sortBy, priceRange, category, onlyForCombos);
    }

    @PostMapping("/validateImage")
    public ResponseEntity<String> validateImage(@RequestParam("image") String base64Image) {
        long maxSizeInBytes = 3 * 1024 * 1024;  // Example: 3 MB limit for image size

        try {
            ImageValidation.validateImageSize(base64Image, maxSizeInBytes);
            return ResponseEntity.ok("Image is valid.");
        } catch (ImageSizeLimitExceededException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    @PostMapping
    public MenuItem addMenuItem(@RequestBody MenuItem menuItem) {
        return menuItemService.addMenuItem(menuItem);
    }

    @PutMapping("/{id}")
    public MenuItem updateMenuItem(@PathVariable Long id, @RequestBody MenuItem updatedItem) {
        return menuItemService.updateMenuItem(id, updatedItem);
    }

    @DeleteMapping("/{id}")
    public void deleteMenuItem(@PathVariable Long id) {
        menuItemService.deleteMenuItem(id);
    }
}


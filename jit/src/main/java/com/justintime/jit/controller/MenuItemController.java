package com.justintime.jit.controller;

import com.justintime.jit.dto.ApiResponse;
import com.justintime.jit.dto.MenuItemDTO;
import com.justintime.jit.dto.PatchRequest;
import com.justintime.jit.entity.Enums.Sort;
import com.justintime.jit.entity.MenuItem;
import com.justintime.jit.exception.ImageSizeLimitExceededException;
import com.justintime.jit.service.JwtService;
import com.justintime.jit.util.ValidationUtils;
import com.justintime.jit.service.MenuItemService;
import org.springframework.beans.factory.annotation.Autowired;
import com.justintime.jit.validators.ValidateInput;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/menu-items")
public class MenuItemController extends BaseController {

    @Autowired
    private MenuItemService menuItemService;

//    @GetMapping
//    public List<MenuItemDTO> getAllMenuItems() {
//        return menuItemService.getAllMenuItems();
//    }

    @GetMapping("/{restaurantCode}")
    @PreAuthorize("hasPermission(null, 'VIEW_MENU_ITEMS')")
    public ResponseEntity<ApiResponse<List<MenuItemDTO>>> getMenuItemsByRestaurant(
            @PathVariable String restaurantCode,
            @RequestParam(required = false) Sort sortBy,
            @RequestParam(required = false) String priceRange,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Boolean onlyVeg,
            @RequestParam(required = false, defaultValue = "false") Boolean onlyForCombos) {
        return success(menuItemService.getMenuItemsByRestaurantId(restaurantCode, sortBy, priceRange, category, onlyVeg, onlyForCombos));
    }

    @GetMapping("/{restaurantCode}/{menuItemName}")
    @PreAuthorize("hasPermission(null, 'VIEW_MENU_ITEMS')")
    public ResponseEntity<ApiResponse<MenuItemDTO>> getMenuItemByRestaurantIdAndMenuItemName(@PathVariable String restaurantCode, @PathVariable String menuItemName) {
        return success(menuItemService.getMenuItemByRestaurantIdAndMenuItemName(restaurantCode, menuItemName));
    }

    @Deprecated
    @PostMapping("/validateImage")
    public ResponseEntity<String> validateImage(@RequestParam("image") String base64Image) {
        long maxSizeInBytes = 3 * 1024 * 1024;  // Example: 3 MB limit for image size

        try {
            ValidationUtils.validateImageSize(base64Image, maxSizeInBytes);
            return ResponseEntity.ok("Image is valid.");
        } catch (ImageSizeLimitExceededException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    @ValidateInput
    @PostMapping("/{restaurantCode}")
    @PreAuthorize("hasPermission(null, 'ADD_MENU_ITEMS')")
    public ResponseEntity<ApiResponse<MenuItem>> addMenuItem(@PathVariable String restaurantCode, @RequestBody MenuItemDTO menuItemDTO) {
        return success(menuItemService.addMenuItem(restaurantCode,menuItemDTO), "Created Menu Item Successfully");
    }

    @ValidateInput
    @PutMapping("/{restaurantCode}/{id}")
    @PreAuthorize("hasPermission(null, 'ADD_MENU_ITEMS')")
    public MenuItem updateMenuItem(@PathVariable String restaurantCode,@PathVariable Long id, @RequestBody MenuItemDTO updatedItem) {
        return menuItemService.updateMenuItem(restaurantCode,id, updatedItem);
    }

    @ValidateInput
    @PatchMapping("/{restaurantCode}/{menuItemName}")
    @PreAuthorize("hasPermission(null, 'ADD_MENU_ITEMS')")
    public ResponseEntity<ApiResponse<MenuItemDTO>> patchUpdateMenuItem(@PathVariable String restaurantCode,@PathVariable String menuItemName, @RequestBody PatchRequest<MenuItemDTO> payload) {
        return success(menuItemService.patchUpdateMenuItem(restaurantCode,menuItemName, payload.getDto(), payload.getPropertiesToBeUpdated()));
    }

    @DeleteMapping("/{restaurantCode}/{menuItemName}")
    @PreAuthorize("hasPermission(null, 'DELETE_MENU_ITEMS')")
    public void deleteMenuItem(@PathVariable String restaurantCode, @PathVariable String menuItemName) {
        menuItemService.deleteMenuItem(restaurantCode, menuItemName);
    }
}


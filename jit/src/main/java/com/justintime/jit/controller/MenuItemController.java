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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/jit-api/menu-items")
public class MenuItemController extends BaseController {

    @Autowired
    private MenuItemService menuItemService;

    @Autowired
    private JwtService jwtService;

//    @GetMapping
//    public List<MenuItemDTO> getAllMenuItems() {
//        return menuItemService.getAllMenuItems();
//    }

    @GetMapping("/{restaurantCode}")
    public ResponseEntity<ApiResponse<List<MenuItemDTO>>> getMenuItemsByRestaurant(
            @PathVariable String restaurantCode,
            @RequestParam(required = false) Sort sortBy,
            @RequestParam(required = false) String priceRange,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Boolean onlyVeg,
            @RequestParam(required = false, defaultValue = "false") Boolean onlyForCombos) {
        return success(menuItemService.getMenuItemsByRestaurantId(restaurantCode, sortBy, priceRange, category, onlyVeg, onlyForCombos));
    }

    @GetMapping("/{restaurantId}/{id}")
    public ResponseEntity<ApiResponse<MenuItemDTO>> getMenuItemByRestaurantIdAndId(@PathVariable Long restaurantId, @PathVariable Long id){
        return success(menuItemService.getMenuItemByRestaurantIdAndId(restaurantId, id));
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

    @PostMapping("/{restaurantCode}")
    public ResponseEntity<ApiResponse<MenuItem>> addMenuItem(@PathVariable String restaurantCode, @RequestBody MenuItemDTO menuItemDTO) {
        validate(menuItemDTO, null, restaurantCode);
        return success(menuItemService.addMenuItem(restaurantCode,menuItemDTO), "Created Menu Item Successfully");
    }

    @PutMapping("/{restaurantCode}/{id}")
    public MenuItem updateMenuItem(@PathVariable String restaurantCode,@PathVariable Long id, @RequestBody MenuItemDTO updatedItem) {
        validate(updatedItem, null, restaurantCode);
        return menuItemService.updateMenuItem(restaurantCode,id, updatedItem);
    }

    @PatchMapping("/{restaurantCode}/{menuItemName}")
    public ResponseEntity<ApiResponse<MenuItemDTO>> patchUpdateMenuItem(@PathVariable String restaurantCode,@PathVariable String menuItemName, @RequestBody PatchRequest<MenuItemDTO> payload) {
        validate(payload.getDto(), payload.getPropertiesToBeUpdated(), restaurantCode);
        return success(menuItemService.patchUpdateMenuItem(restaurantCode,menuItemName, payload.getDto(), payload.getPropertiesToBeUpdated()));
    }

    @DeleteMapping("/{restaurantCode}/{menuItemName}")
    public void deleteMenuItem(@PathVariable String restaurantCode, @PathVariable String menuItemName) {
        menuItemService.deleteMenuItem(restaurantCode, menuItemName);
    }
}


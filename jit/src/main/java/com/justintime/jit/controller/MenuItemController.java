package com.justintime.jit.controller;

import com.justintime.jit.dto.ApiResponse;
import com.justintime.jit.dto.MenuItemDTO;
import com.justintime.jit.dto.PatchRequest;
import com.justintime.jit.entity.Enums.Sort;
import com.justintime.jit.entity.MenuItem;
import com.justintime.jit.exception.ImageSizeLimitExceededException;
import com.justintime.jit.service.JwtService;
import com.justintime.jit.util.ImageValidation;
import com.justintime.jit.service.MenuItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

    @GetMapping
    public ResponseEntity<ApiResponse<List<MenuItemDTO>>> getMenuItemsByRestaurant(
            @AuthenticationPrincipal Long restaurantId,
            @RequestParam(required = false) Sort sortBy,
            @RequestParam(required = false) String priceRange,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Boolean onlyVeg,
            @RequestParam(required = false, defaultValue = "false") Boolean onlyForCombos) {
        return success(menuItemService.getMenuItemsByRestaurantId(restaurantId, sortBy, priceRange, category, onlyVeg, onlyForCombos));
    }

    @GetMapping("/{restaurantId}/{id}")
    public ResponseEntity<ApiResponse<MenuItemDTO>> getMenuItemByRestaurantIdAndId(@PathVariable Long restaurantId, @PathVariable Long id){
        return success(menuItemService.getMenuItemByRestaurantIdAndId(restaurantId, id));
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
    public ResponseEntity<ApiResponse<MenuItem>> addMenuItem(@AuthenticationPrincipal Long restaurantId, @RequestBody MenuItemDTO menuItemDTO) {
        return success(menuItemService.addMenuItem(restaurantId,menuItemDTO), "Created Menu Item Successfully");
    }

    @PutMapping("/{id}")
    public MenuItem updateMenuItem(@AuthenticationPrincipal Long restaurantId,@PathVariable Long id, @RequestBody MenuItemDTO updatedItem) {
        return menuItemService.updateMenuItem(restaurantId,id, updatedItem);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<MenuItem>> patchUpdateMenuItem(@AuthenticationPrincipal Long restaurantId,@PathVariable Long id, @RequestBody PatchRequest<MenuItemDTO> payload) {
        return success(menuItemService.patchUpdateMenuItem(restaurantId,id, payload.getDto(), payload.getPropertiesToBeUpdated()));
    }

    @DeleteMapping("/{id}")
    public void deleteMenuItem(@PathVariable Long id) {
        menuItemService.deleteMenuItem(id);
    }
}


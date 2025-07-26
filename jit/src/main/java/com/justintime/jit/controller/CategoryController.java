package com.justintime.jit.controller;

import com.justintime.jit.dto.ApiResponse;
import com.justintime.jit.dto.CategoryDTO;
import com.justintime.jit.dto.PatchRequest;
import com.justintime.jit.service.CategoryService;
import com.justintime.jit.entity.Category;
import com.justintime.jit.validators.ValidateInput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/jit-api/categories")
public class CategoryController extends BaseController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/getAll/{restaurantCode}")
    @PreAuthorize("hasPermission(null, 'VIEW_MENU_ITEMS')")
    public ResponseEntity<ApiResponse<List<CategoryDTO>>> getAllCategories(@PathVariable String restaurantCode) {
        return success(categoryService.getAllCategories(restaurantCode));
    }

    @GetMapping("/{restaurantId}/{id}")
    @PreAuthorize("hasPermission(null, 'VIEW_MENU_ITEMS')")
    public ResponseEntity<ApiResponse<CategoryDTO>> getCategoryByRestaurantIdAndId(@PathVariable Long restaurantId, @PathVariable Long id) {
        Optional<CategoryDTO> categoryDTO = categoryService.getCategoryByRestaurantIdAndId(restaurantId, id);
        if(categoryDTO.isPresent()) {
            return success(categoryDTO.get());
        }
        return error("Category doesnot exisit", HttpStatus.NOT_FOUND);
    }

    @ValidateInput
    @PostMapping("/{restaurantCode}")
    @PreAuthorize("hasPermission(null, 'ADD_MENU_ITEMS')")
    public ResponseEntity<ApiResponse<Category>> createCategory(@PathVariable String restaurantCode,@RequestBody CategoryDTO categoryDTO) {
        return success(categoryService.createCategory(restaurantCode, categoryDTO), "Category Created Successfully");
    }

    @ValidateInput
    @PutMapping("/{restaurantCode}/{categoryName}")
    @PreAuthorize("hasPermission(null, 'ADD_MENU_ITEMS')")
    public ResponseEntity<ApiResponse<CategoryDTO>> updateCategory(@PathVariable String restaurantCode, @PathVariable String categoryName, @RequestBody CategoryDTO categoryDTO) {
        return success(categoryService.updateCategory(restaurantCode, categoryName, categoryDTO));
    }

    @ValidateInput
    @PatchMapping("/{restaurantCode}/{categoryName}")
    @PreAuthorize("hasPermission(null, 'ADD_MENU_ITEMS')")
    public ResponseEntity<ApiResponse<CategoryDTO>> patchUpdateCategory(@PathVariable String restaurantCode,@PathVariable String categoryName, @RequestBody PatchRequest<CategoryDTO> payload) {
        return success(categoryService.patchUpdateCategory(restaurantCode, categoryName, payload.getDto(), payload.getPropertiesToBeUpdated()));
    }

    @DeleteMapping("/{restaurantCode}/{categoryName}")
    @PreAuthorize("hasPermission(null, 'DELETE_MENU_ITEMS')")
    public ResponseEntity<ApiResponse<String>> deleteCategory(@PathVariable String restaurantCode, @PathVariable String categoryName) {
        categoryService.deleteCategory(restaurantCode, categoryName);
        return success(null, "Category Deleted successfully");
    }
}


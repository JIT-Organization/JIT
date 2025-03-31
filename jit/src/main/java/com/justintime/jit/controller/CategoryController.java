package com.justintime.jit.controller;

import com.justintime.jit.dto.CategoryDTO;
import com.justintime.jit.dto.PatchRequest;
import com.justintime.jit.service.CategoryService;
import com.justintime.jit.entity.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/jit-api/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/getAll")
    public List<CategoryDTO> getAllCategories(@AuthenticationPrincipal Long restaurantId) {
        return categoryService.getAllCategories(restaurantId);
    }

    @GetMapping("/{restaurantId}/{id}")
    public ResponseEntity<CategoryDTO> getCategoryByRestaurantIdAndId(@PathVariable Long restaurantId, @PathVariable Long id) {
        return categoryService.getCategoryByRestaurantIdAndId(restaurantId, id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Category> createCategory(@AuthenticationPrincipal Long restaurantId,@RequestBody CategoryDTO categoryDTO) {
        return ResponseEntity.ok(categoryService.createCategory(restaurantId,categoryDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Category> updateCategory(@AuthenticationPrincipal Long restaurantId,@PathVariable Long id, @RequestBody CategoryDTO categoryDTO) {
        return ResponseEntity.ok(categoryService.updateCategory(restaurantId,id, categoryDTO));
    }

    @PatchMapping("/{id}")
    public Category patchUpdateCategory(@AuthenticationPrincipal Long restaurantId,@PathVariable Long id, @RequestBody PatchRequest<CategoryDTO> payload) {
        return categoryService.patchUpdateCategory(restaurantId,id, payload.getDto(), payload.getPropertiesToBeUpdated());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok("Category deleted successfully");
    }
}


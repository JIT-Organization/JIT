package com.justintime.jit.controller;

import com.justintime.jit.dto.CategoryDTO;
import com.justintime.jit.dto.PatchRequest;
import com.justintime.jit.service.CategoryService;
import com.justintime.jit.entity.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/jit-api/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/getAll/{restaurantId}")
    public List<CategoryDTO> getAllCategories(@PathVariable Long restaurantId) {
        return categoryService.getAllCategories(restaurantId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryDTO> getCategoryById(@PathVariable Long id) {
        return categoryService.getCategoryById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{restaurantId}")
    public ResponseEntity<Category> createCategory(@PathVariable Long restaurantId,@RequestBody CategoryDTO categoryDTO) {
        return ResponseEntity.ok(categoryService.createCategory(restaurantId,categoryDTO));
    }

    @PutMapping("/{restaurantId}/{id}")
    public ResponseEntity<Category> updateCategory(@PathVariable Long restaurantId,@PathVariable Long id, @RequestBody CategoryDTO categoryDTO) {
        return ResponseEntity.ok(categoryService.updateCategory(restaurantId,id, categoryDTO));
    }

    @PatchMapping("/{restaurantId}/{id}")
    public Category patchUpdateCategory(@PathVariable Long restaurantId,@PathVariable Long id, @RequestBody PatchRequest<CategoryDTO> payload) {
        return categoryService.patchUpdateCategory(restaurantId,id, payload.getDto(), payload.getPropertiesToBeUpdated());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok("Category deleted successfully");
    }
}


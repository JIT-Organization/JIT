//package com.justintime.jit.controller;
//
//import com.justintime.jit.dto.ApiResponse;
//import com.justintime.jit.dto.CategoryDTO;
//import com.justintime.jit.dto.PatchRequest;
//import com.justintime.jit.entity.Category;
//import com.justintime.jit.service.CategoryService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//import java.util.Optional;
//
//public class BatchController extends BaseController{
//    @Autowired
//    private CategoryService categoryService;
//
//    @GetMapping("/getAll/{restaurantCode}")
//    public ResponseEntity<ApiResponse<List<CategoryDTO>>> getAllCategories(@PathVariable String restaurantCode) {
//        return success(categoryService.getAllCategories(restaurantCode));
//    }
//
//    @GetMapping("/{restaurantId}/{id}")
//    public ResponseEntity<ApiResponse<CategoryDTO>> getCategoryByRestaurantIdAndId(@PathVariable Long restaurantId, @PathVariable Long id) {
//        Optional<CategoryDTO> categoryDTO = categoryService.getCategoryByRestaurantIdAndId(restaurantId, id);
//        if(categoryDTO.isPresent()) {
//            return success(categoryDTO.get());
//        }
//        return error("Category doesnot exisit", HttpStatus.NOT_FOUND);
//    }
//
//    @PostMapping
//    public ResponseEntity<ApiResponse<Category>> createCategory(@AuthenticationPrincipal Long restaurantId, @RequestBody CategoryDTO categoryDTO) {
//        return success(categoryService.createCategory(restaurantId,categoryDTO), "Category Created Successfully");
//    }
//
//    @PutMapping("/{id}")
//    public ResponseEntity<Category> updateCategory(@AuthenticationPrincipal Long restaurantId,@PathVariable Long id, @RequestBody CategoryDTO categoryDTO) {
//        return ResponseEntity.ok(categoryService.updateCategory(restaurantId,id, categoryDTO));
//    }
//
//    @PatchMapping("/{id}")
//    public ResponseEntity<ApiResponse<Category>> patchUpdateCategory(@AuthenticationPrincipal Long restaurantId,@PathVariable Long id, @RequestBody PatchRequest<CategoryDTO> payload) {
//        return success(categoryService.patchUpdateCategory(restaurantId,id, payload.getDto(), payload.getPropertiesToBeUpdated()));
//    }
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity<String> deleteCategory(@PathVariable Long id) {
//        categoryService.deleteCategory(id);
//        return ResponseEntity.ok("Category deleted successfully");
//    }
//}

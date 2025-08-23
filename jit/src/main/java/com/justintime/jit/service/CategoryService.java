package com.justintime.jit.service;

import com.justintime.jit.dto.CategoryDTO;
import com.justintime.jit.entity.Category;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

public interface CategoryService extends BaseService<Category,Long>{
    List<CategoryDTO> getAllCategories(String restaurantCode);
    Category createCategory(String restaurantCode,CategoryDTO categoryDTO);
    Optional<CategoryDTO> getCategoryByRestaurantIdAndId(Long restaurantId, Long id);
    CategoryDTO updateCategory(String restaurantCode, String categoryName, CategoryDTO updatedCategoryDTO);
    CategoryDTO patchUpdateCategory(String restaurantCode, String categoryName, CategoryDTO categoryDTO, HashSet<String> propertiesToBeUpdated);
    void deleteCategory(String restaurantCode, String categoryName);
}

package com.justintime.jit.service;

import com.justintime.jit.dto.CategoryDTO;
import com.justintime.jit.entity.Category;
import java.util.List;
import java.util.Optional;

public interface CategoryService extends BaseService<Category,Long>{
    List<CategoryDTO> getAllCategories(Long restaurantId);
    Category createCategory(Long restaurantId,CategoryDTO categoryDTO);
    Optional<CategoryDTO> getCategoryByRestaurantIdAndId(Long restaurantId, Long id);
    Category updateCategory(Long restaurantId,Long id, CategoryDTO updatedCategoryDTO);
    Category patchUpdateCategory(Long restaurantId,Long id,CategoryDTO categoryDTO,List<String>propertiesToBeUpdated);
    void deleteCategory(Long id);
}

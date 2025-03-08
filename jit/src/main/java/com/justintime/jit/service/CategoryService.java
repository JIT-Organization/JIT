package com.justintime.jit.service;

import com.justintime.jit.dto.CategoryDTO;
import com.justintime.jit.entity.Category;
import java.util.List;
import java.util.Optional;

public interface CategoryService extends BaseService<Category,Long>{
    List<CategoryDTO> getAllCategories(Long restaurantId);
    Category createCategory(Category category);
    Optional<CategoryDTO> getCategoryById(Long id);
    Category updateCategory(Long id, Category updatedCategory);
    Category patchUpdateCategory(Long id,CategoryDTO categoryDTO,List<String>propertiesToBeUpdated);
    void deleteCategory(Long id);
}

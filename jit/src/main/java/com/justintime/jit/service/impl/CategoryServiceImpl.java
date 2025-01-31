package com.justintime.jit.service.impl;

import com.justintime.jit.entity.Category;
import com.justintime.jit.repository.CategoryRepository;
import com.justintime.jit.service.CategoryService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImpl extends BaseServiceImpl<Category,Long> implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Optional<Category> getCategoryById(Long id) {
        return categoryRepository.findById(id);
    }

    @Transactional
    public Category createCategory(Category category) {
        if (categoryRepository.existsByCategoryName(category.getCategoryName())) {
            throw new RuntimeException("Category name already exists!");
        }
        return categoryRepository.save(category);
    }

    public Category updateCategory(Long id, Category updatedCategory) {
        return categoryRepository.findById(id).map(existingCategory -> {
            existingCategory.setCategoryName(updatedCategory.getCategoryName());
            existingCategory.setFoods(updatedCategory.getFoods());
            existingCategory.setUpdatedDttm(updatedCategory.getUpdatedDttm());
            return categoryRepository.save(existingCategory);
        }).orElseThrow(() -> new RuntimeException("Category not found with id " + id));
    }

    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new RuntimeException("Category not found with id " + id);
        }
        categoryRepository.deleteById(id);
    }

}

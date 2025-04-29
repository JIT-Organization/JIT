package com.justintime.jit.service.impl;

import com.justintime.jit.dto.CategoryDTO;
import com.justintime.jit.entity.Category;
import com.justintime.jit.entity.ComboEntities.Combo;
import com.justintime.jit.entity.MenuItem;
import com.justintime.jit.exception.ResourceNotFoundException;
import com.justintime.jit.repository.CategoryRepository;
import com.justintime.jit.repository.ComboRepo.ComboRepository;
import com.justintime.jit.repository.MenuItemRepository;
import com.justintime.jit.repository.RestaurantRepository;
import com.justintime.jit.service.CategoryService;
import com.justintime.jit.util.CommonServiceImplUtil;
import com.justintime.jit.util.mapper.GenericMapper;
import com.justintime.jit.util.mapper.MapperFactory;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class CategoryServiceImpl extends BaseServiceImpl<Category,Long> implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private MenuItemRepository menuItemRepository;
    @Autowired
    private ComboRepository comboRepository;
    @Autowired
    private RestaurantRepository restaurantRepository;
    @Autowired
    private CommonServiceImplUtil commonServiceImplUtil;

    private final GenericMapper<Category, CategoryDTO> mapper = MapperFactory.getMapper(Category.class, CategoryDTO.class);


    public List<CategoryDTO> getAllCategories(String restaurantCode) {
        List<Category> categories= categoryRepository.findByRestaurant_RestaurantCode(restaurantCode);
        return categories.stream()
                .map(category -> mapToDTO(category, mapper))
                .collect(Collectors.toList());
    }

    private CategoryDTO mapToDTO(Category category, GenericMapper<Category, CategoryDTO> mapper) {
        CategoryDTO dto = mapper.toDto(category);
        dto.setFoodItems(
                Stream.concat(
                        category.getMenuItemSet().stream().map(MenuItem::getName),
                        category.getComboSet().stream().map(Combo::getComboName)
                ).collect(Collectors.collectingAndThen(Collectors.toSet(), Collections::unmodifiableSet))
        );
        dto.setFoodCount(dto.getFoodItems().size());
        return dto;
    }

    public Optional<CategoryDTO> getCategoryByRestaurantIdAndId(Long restaurantId, Long id) {
        Optional<Category> category = categoryRepository.findByRestaurantIdAndId(restaurantId, id);
        return category.map(cat -> mapToDTO(cat, mapper));
    }


    @Transactional
    public Category createCategory(String restaurantCode, CategoryDTO categoryDTO) {
        if (categoryRepository.existsByCategoryNameAndRestaurant_RestaurantCode(categoryDTO.getCategoryName(), restaurantCode)) {
            throw new RuntimeException("Category name already exists!");
        }
        Category category = mapper.toEntity(categoryDTO);
        category.setRestaurant(restaurantRepository.findByRestaurantCode(restaurantCode).orElseThrow(() -> new RuntimeException("Restaurant not found")));
        resolveRelationships(category, categoryDTO);
        return categoryRepository.save(category);
    }

    public CategoryDTO updateCategory(String restaurantCode, String categoryName, CategoryDTO updatedCategoryDTO) {
        Category exisitingCategory = categoryRepository.findByCategoryNameAndRestaurant_RestaurantCode(categoryName, restaurantCode)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        Category updatedCategory = mapper.toEntity(updatedCategoryDTO);
        updatedCategory.setRestaurant(exisitingCategory.getRestaurant());
        resolveRelationships(updatedCategory, updatedCategoryDTO);
        categoryRepository.save(updatedCategory);
        return mapToDTO(updatedCategory, mapper);
    }


    @Override
    public CategoryDTO patchUpdateCategory(String restaurantCode, String categoryName, CategoryDTO categoryDTO, HashSet<String> propertiesToBeUpdated) {
        Category existingCategory = categoryRepository.findByCategoryNameAndRestaurant_RestaurantCode(categoryName, restaurantCode)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        Category patchedCategory = mapper.toEntity(categoryDTO);
        if(propertiesToBeUpdated.contains("categoryName")) {
            patchedCategory.setRestaurant(existingCategory.getRestaurant());
        }
        resolveRelationships(patchedCategory, categoryDTO);
        HashSet<String> propertiesToBeChangedClone = new HashSet<>(propertiesToBeUpdated);
        if(propertiesToBeChangedClone.contains("foodItems")) {
            propertiesToBeChangedClone.remove("foodItems");
            propertiesToBeChangedClone.add("menuItemSet");
            propertiesToBeChangedClone.add("comboSet");
        }
        commonServiceImplUtil.copySelectedProperties(patchedCategory, existingCategory, propertiesToBeChangedClone);
        existingCategory.setUpdatedDttm(LocalDateTime.now());
        categoryRepository.save(existingCategory);
        return mapToDTO(existingCategory, mapper);
    }

    public void deleteCategory(String restaurantCode, String categoryName) {
        Category existingCategory = categoryRepository.findByCategoryNameAndRestaurant_RestaurantCode(categoryName, restaurantCode).orElseThrow(() -> new RuntimeException("Category not found"));
        existingCategory.setComboSet(null);
        existingCategory.setMenuItemSet(null);
        categoryRepository.deleteById(existingCategory.getId());
    }

    private void resolveRelationships(Category category, CategoryDTO categoryDTO) {

        if (categoryDTO.getFoodItems() != null) {
            Set<MenuItem> menuItems = menuItemRepository.findByMenuItemNamesAndRestaurantId(
                    categoryDTO.getFoodItems(), category.getRestaurant().getId());
            Set<Combo> combos = comboRepository.findByComboNamesAndRestaurantId(
                    categoryDTO.getFoodItems(), category.getRestaurant().getId());
            category.setMenuItemSet(menuItems);
            category.setComboSet(combos);
        }
    }
}

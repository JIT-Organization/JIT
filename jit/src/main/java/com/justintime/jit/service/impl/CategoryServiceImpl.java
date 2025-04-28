package com.justintime.jit.service.impl;

import com.justintime.jit.dto.CategoryDTO;
import com.justintime.jit.entity.Category;
import com.justintime.jit.entity.ComboEntities.Combo;
import com.justintime.jit.entity.MenuItem;
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

    public List<CategoryDTO> getAllCategories(String restaurantCode) {
        List<Category> categories= categoryRepository.findByRestaurant_RestaurantCode(restaurantCode);
        GenericMapper<Category, CategoryDTO> mapper = MapperFactory.getMapper(Category.class, CategoryDTO.class);
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
        GenericMapper<Category, CategoryDTO> mapper = MapperFactory.getMapper(Category.class, CategoryDTO.class);
        return category.map(cat -> mapToDTO(cat, mapper));
    }


    @Transactional
    public Category createCategory(Long restaurantId, CategoryDTO categoryDTO) {
        if (categoryRepository.existsByCategoryNameAndRestaurantId(categoryDTO.getCategoryName(),restaurantId)) {
            throw new RuntimeException("Category name already exists!");
        }
        GenericMapper<Category, CategoryDTO> mapper = MapperFactory.getMapper(Category.class, CategoryDTO.class);
        Category category = mapper.toEntity(categoryDTO);
        category.setRestaurant(restaurantRepository.findById(restaurantId).orElseThrow(() -> new RuntimeException("Restaurant not found")));
        resolveRelationships(category, categoryDTO);
        return categoryRepository.save(category);
    }

    public Category updateCategory(Long restaurantId,Long id, CategoryDTO updatedCategoryDTO) {
        return categoryRepository.findById(id).map(category1 -> {
            GenericMapper<Category, CategoryDTO> mapper = MapperFactory.getMapper(Category.class, CategoryDTO.class);
            Category updatedCategory = mapper.toEntity(updatedCategoryDTO);
            updatedCategory.setRestaurant(restaurantRepository.findById(restaurantId).orElseThrow(() -> new RuntimeException("Restaurant not found")));
            updatedCategory.setId(id);
            resolveRelationships(updatedCategory, updatedCategoryDTO);
            updatedCategory.setUpdatedDttm(LocalDateTime.now());

            return categoryRepository.save(updatedCategory);
        }).orElseThrow(() -> new RuntimeException("Category not found with id " + id));
    }


    @Override
    public Category patchUpdateCategory(Long restaurantId, Long id, CategoryDTO categoryDTO, HashSet<String> propertiesToBeUpdated) {
        Category existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        GenericMapper<Category, CategoryDTO> mapper = MapperFactory.getMapper(Category.class, CategoryDTO.class).setSkipNullEnabled(true);
        Category patchedCategory = mapper.toEntity(categoryDTO);
        patchedCategory.setRestaurant(restaurantRepository.findById(restaurantId).orElseThrow(() -> new RuntimeException("Restaurant not found")));
        resolveRelationships(patchedCategory, categoryDTO);
        HashSet<String> propertiesToBeChangedClone = new HashSet<>(propertiesToBeUpdated);
        if(propertiesToBeChangedClone.contains("foodItems")) {
            propertiesToBeChangedClone.remove("foodItems");
            propertiesToBeChangedClone.add("menuItemSet");
            propertiesToBeChangedClone.add("comboSet");
        }
        commonServiceImplUtil.copySelectedProperties(patchedCategory, existingCategory, propertiesToBeChangedClone);
        existingCategory.setUpdatedDttm(LocalDateTime.now());
        return categoryRepository.save(existingCategory);
    }

    public void deleteCategory(Long id) {
        Category existingCategory = categoryRepository.findById(id).orElseThrow(() -> new RuntimeException("Category not found"));
        existingCategory.setComboSet(null);
        existingCategory.setMenuItemSet(null);
        categoryRepository.deleteById(id);
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

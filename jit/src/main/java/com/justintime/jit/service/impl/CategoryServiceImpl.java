package com.justintime.jit.service.impl;

import com.justintime.jit.dto.CategoryDTO;
import com.justintime.jit.dto.MenuItemDTO;
import com.justintime.jit.entity.Category;
import com.justintime.jit.entity.ComboEntities.Combo;
import com.justintime.jit.entity.MenuItem;
import com.justintime.jit.repository.CategoryRepository;
import com.justintime.jit.repository.ComboRepo.ComboRepository;
import com.justintime.jit.repository.MenuItemRepository;
import com.justintime.jit.repository.RestaurantRepository;
import com.justintime.jit.service.CategoryService;
import com.justintime.jit.util.mapper.GenericMapper;
import com.justintime.jit.util.mapper.MapperFactory;
import jakarta.transaction.Transactional;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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

    public List<CategoryDTO> getAllCategories(Long restaurantId) {
        List<Category> categories= categoryRepository.findByRestaurantId(restaurantId);
        GenericMapper<Category, CategoryDTO> mapper = MapperFactory.getMapper(Category.class, CategoryDTO.class);
        return categories.stream()
                .map(category -> mapToDTO(category, mapper))
                .collect(Collectors.toList());

    }

    private CategoryDTO mapToDTO(Category category, GenericMapper<Category, CategoryDTO> mapper) {
        CategoryDTO dto = mapper.toDto(category);
        dto.setMenuItemSet(category.getMenuItems().stream()
                .map(MenuItem::getName)
                .collect(Collectors.toSet()));
        dto.setComboSet(category.getCombos().stream()
                .map(Combo::getComboName)
                .collect(Collectors.toSet()));

        return dto;
    }

    public Optional<CategoryDTO> getCategoryById(Long id) {
        Optional<Category> category = categoryRepository.findById(id);
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
            resolveRelationships(updatedCategory, updatedCategoryDTO);
            updatedCategory.setId(id);
            updatedCategory.setUpdatedDttm(LocalDateTime.now());

            return categoryRepository.save(updatedCategory);
        }).orElseThrow(() -> new RuntimeException("Category not found with id " + id));
    }


    @Override
    public Category patchUpdateCategory(Long restaurantId,Long id, CategoryDTO categoryDTO, List<String> propertiesToBeUpdated) {
        Category existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        GenericMapper<Category, CategoryDTO> mapper = MapperFactory.getMapper(Category.class, CategoryDTO.class).setSkipNullEnabled(true);
        Category patchedCategory = mapper.toEntity(categoryDTO);
        patchedCategory.setRestaurant(restaurantRepository.findById(restaurantId).orElseThrow(() -> new RuntimeException("Restaurant not found")));
        resolveRelationships(patchedCategory, categoryDTO);
        copySelectedProperties(patchedCategory, existingCategory, propertiesToBeUpdated);
        existingCategory.setUpdatedDttm(LocalDateTime.now());
        return categoryRepository.save(existingCategory);
    }

    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new RuntimeException("Category not found with id " + id);
        }
        categoryRepository.deleteById(id);
    }

    private void resolveRelationships(Category category, CategoryDTO categoryDTO) {

        if (categoryDTO.getMenuItemSet() != null) {
            Set<MenuItem> menuItems = menuItemRepository.findByMenuItemNamesAndCategoryId(
                    categoryDTO.getMenuItemSet(), categoryDTO.getId());
            category.setMenuItems(menuItems);
        }
        if (categoryDTO.getComboSet() != null) {
            category.setCombos(categoryDTO.getComboSet().stream()
                    .map(comboName -> comboRepository.findByComboNameAndCategoryId(comboName, category.getId()))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet()));
        }}

        private void copySelectedProperties(Object source, Object target, List<String> propertiesToBeChanged){
            BeanWrapper srcWrapper = new BeanWrapperImpl(source);
            BeanWrapper targetWrapper = new BeanWrapperImpl(target);

            for (String property : propertiesToBeChanged) {
                if (srcWrapper.isReadableProperty(property) && srcWrapper.getPropertyValue(property) != null) {
                    targetWrapper.setPropertyValue(property, srcWrapper.getPropertyValue(property));
                }
            }}
}

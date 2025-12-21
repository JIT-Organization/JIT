package com.justintime.jit.validators.rules;

import com.justintime.jit.dto.CategoryDTO;
import com.justintime.jit.repository.CategoryRepository;
import com.justintime.jit.util.sanitization.SanitizationUtils;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CategoryRules extends BaseRules {

    @Autowired
    private CategoryRepository categoryRepository;

    @PostConstruct
    public void registerCategoryRules() {
        registerFieldRules();
        registerBusinessRules();
    }

    private void registerFieldRules() {
        addFieldRule(CategoryDTO.class, "categoryName", dto -> SanitizationUtils.isBlank(dto.getCategoryName()), "Category name cannot be blank");
    }

    private void registerBusinessRules() {
        addBusinessRule(CategoryDTO.class, "categoryName", dto -> categoryRepository.existsByCategoryNameAndRestaurant_RestaurantCode(dto.getCategoryName(), jwtBean.getRestaurantCode()), "Category already exists for this restaurant");
    }
}

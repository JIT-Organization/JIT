package com.justintime.jit.validators;

import com.justintime.jit.dto.CategoryDTO;
import com.justintime.jit.repository.CategoryRepository;
import com.justintime.jit.util.sanitization.SanitizationUtils;
import jakarta.annotation.Nullable;
import org.modelmapper.ValidationException;
import org.modelmapper.spi.ErrorMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static com.justintime.jit.util.ValidationUtils.runValidation;
import static com.justintime.jit.util.ValidationUtils.shouldValidate;

@Component
public class CategoryRequestValidator implements RequestValidator<CategoryDTO> {

    @Autowired
    CategoryRepository categoryRepository;

    @Override
    public void validate(CategoryDTO dto, @Nullable Set<String> fieldsToValidate, @Nullable String restaurantCode) throws ValidationException {
        List<ErrorMessage> errors = new ArrayList<>();

        List<ValidationRule> allRules = List.of(
                new ValidationRule("categoryName", () -> {
                    if (SanitizationUtils.isBlank(dto.getCategoryName())) {
                        errors.add(new ErrorMessage("Category name cannot be blank or invalid."));
                    } else if(categoryRepository.existsByCategoryNameAndRestaurant_RestaurantCode(dto.getCategoryName(), restaurantCode)) {
                        errors.add(new ErrorMessage("Category exists with this name."));
                    }
                })
        );

        runValidation(allRules, fieldsToValidate);

        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }
}

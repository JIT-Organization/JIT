package com.justintime.jit.validators;

import jakarta.annotation.Nullable;
import com.justintime.jit.exception.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class ValidationDispatcher {

//    private final Map<Class<?>, RequestValidator<?>> validators = new HashMap<>();
//
//    @Autowired
//    public ValidationDispatcher(List<RequestValidator<?>> validatorList) {
//        for (RequestValidator<?> validator : validatorList) {
//            Class<?> clazz = GenericTypeResolver.resolveTypeArgument(validator.getClass(), RequestValidator.class);
//            if (clazz != null) {
//                validators.put(clazz, validator);
//            }
//        }
//    }

    @SuppressWarnings("unchecked")
//    public <T> void process(T dto, @Nullable Set<String> fieldsToValidate, @Nullable String restaurantCode) {
//        RequestValidator<T> validator = (RequestValidator<T>) validators.get(dto.getClass());
//        if (validator == null) {
//            throw new IllegalArgumentException("No validator found for " + dto.getClass().getSimpleName());
//        }
//        validator.validate(dto, fieldsToValidate, restaurantCode);
//    }
    private final ValidationRuleRegistry registry;

    @Autowired
    public ValidationDispatcher(ValidationRuleRegistry registry) {
        this.registry = registry;
    }

    public <T> void process(T dto, @Nullable Set<String> fieldsToValidate) throws ValidationException {
        List<ValidationError> errors = new ArrayList<>();

        // Field validation
        for (ValidationRule<?> rule : registry.getFieldRules(dto.getClass())) {
            @SuppressWarnings("unchecked")
            ValidationRule<T> typedRule = (ValidationRule<T>) rule;

            if (fieldsToValidate == null || fieldsToValidate.contains(typedRule.getFieldName())) {
                typedRule.validate(dto).ifPresent(errors::add);
            }
        }

        // Business validation
        for (ValidationRule<?> rule : registry.getBusinessRules(dto.getClass())) {
            @SuppressWarnings("unchecked")
            ValidationRule<T> typedRule = (ValidationRule<T>) rule;

            if (fieldsToValidate == null || fieldsToValidate.contains(typedRule.getFieldName())) {
                typedRule.validate(dto).ifPresent(errors::add);
            }
        }

        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }
}


package com.justintime.jit.validators;

import jakarta.annotation.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.GenericTypeResolver;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class ValidationDispatcher {

    private final Map<Class<?>, RequestValidator<?>> validators = new HashMap<>();

    @Autowired
    public ValidationDispatcher(List<RequestValidator<?>> validatorList) {
        for (RequestValidator<?> validator : validatorList) {
            Class<?> clazz = GenericTypeResolver.resolveTypeArgument(validator.getClass(), RequestValidator.class);
            if (clazz != null) {
                validators.put(clazz, validator);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public <T> void process(T dto, @Nullable Set<String> fieldsToValidate, @Nullable String restaurantCode) {
        RequestValidator<T> validator = (RequestValidator<T>) validators.get(dto.getClass());
        if (validator == null) {
            throw new IllegalArgumentException("No validator found for " + dto.getClass().getSimpleName());
        }
        validator.validate(dto, fieldsToValidate, restaurantCode);
    }
}


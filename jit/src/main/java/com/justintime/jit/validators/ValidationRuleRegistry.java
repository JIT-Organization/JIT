package com.justintime.jit.validators;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ValidationRuleRegistry {

    private final Map<Class<?>, List<ValidationRule<?>>> fieldRules = new HashMap<>();
    private final Map<Class<?>, List<ValidationRule<?>>> businessRules = new HashMap<>();

    public <T> void registerFieldRule(Class<T> dtoClass, ValidationRule<T> rule) {
        fieldRules.computeIfAbsent(dtoClass, k -> new ArrayList<>()).add(rule);
    }

    public <T> void registerBusinessRule(Class<T> dtoClass, ValidationRule<T> rule) {
        businessRules.computeIfAbsent(dtoClass, k -> new ArrayList<>()).add(rule);
    }

    public List<ValidationRule<?>> getFieldRules(Class<?> dtoClass) {
        return fieldRules.getOrDefault(dtoClass, List.of());
    }

    public List<ValidationRule<?>> getBusinessRules(Class<?> dtoClass) {
        return businessRules.getOrDefault(dtoClass, List.of());
    }
}

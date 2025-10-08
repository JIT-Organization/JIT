package com.justintime.jit.validators.rules;

import com.justintime.jit.bean.JwtBean;
import com.justintime.jit.validators.Severity;
import com.justintime.jit.validators.ValidationError;
import com.justintime.jit.validators.ValidationRuleRegistry;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;
import java.util.function.Predicate;

public abstract class BaseRules {

    @Autowired
    protected JwtBean jwtBean;

    @Autowired
    protected ValidationRuleRegistry registry;

    protected <T> void addFieldRule(Class<T> dtoClass, String field, Predicate<T> condition, String message) {
        registry.registerFieldRule(dtoClass, dto ->
                condition.test(dto)
                        ? Optional.of(new ValidationError(field, message, Severity.ERROR))
                        : Optional.empty());
    }

    protected <T> void addBusinessRule(Class<T> dtoClass, String field, Predicate<T> condition, String message) {
        registry.registerBusinessRule(dtoClass, dto ->
                condition.test(dto)
                        ? Optional.of(new ValidationError(field, message, Severity.ERROR))
                        : Optional.empty());
    }
}

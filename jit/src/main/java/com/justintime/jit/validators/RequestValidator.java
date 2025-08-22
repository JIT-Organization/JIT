package com.justintime.jit.validators;

import jakarta.annotation.Nullable;
import org.modelmapper.ValidationException;

import java.util.Set;

public interface RequestValidator<T> {
    void validate(T dto, @Nullable Set<String> fieldsToValidate, @Nullable String restaurantCode) throws ValidationException;
}

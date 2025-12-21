package com.justintime.jit.validators;

import java.util.Optional;

@FunctionalInterface
public interface ValidationRule<T> {
    Optional<ValidationError> validate(T dto);

    default String getFieldName() { return null; }
    default Severity getSeverity() { return Severity.ERROR; }
}

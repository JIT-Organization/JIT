package com.justintime.jit.exception;

import com.justintime.jit.validators.ValidationError;
import lombok.Getter;

import java.util.List;

import java.util.Collections;

@Getter
public class ValidationException extends RuntimeException {

    private final List<ValidationError> errors;

    public ValidationException(List<ValidationError> errors) {
        super(buildMessage(errors));
        this.errors = errors != null ? List.copyOf(errors) : Collections.emptyList();
    }

    private static String buildMessage(List<ValidationError> errors) {
        if (errors == null || errors.isEmpty()) {
            return "Validation failed with no error details.";
        }

        StringBuilder sb = new StringBuilder("Validation failed: ");
        for (ValidationError error : errors) {
            sb.append("[").append(error.fieldName()).append(": ")
                    .append(error.message()).append("] ");
        }
        return sb.toString();
    }
}

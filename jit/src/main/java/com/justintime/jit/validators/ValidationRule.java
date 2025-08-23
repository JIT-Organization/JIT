package com.justintime.jit.validators;

public record ValidationRule(String fieldName, Runnable validationLogic) {
}

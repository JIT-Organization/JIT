package com.justintime.jit.validators;

public record ValidationError(String fieldName, String message, Severity severity) {}


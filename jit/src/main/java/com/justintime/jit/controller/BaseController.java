package com.justintime.jit.controller;

import com.justintime.jit.dto.ApiResponse;
import com.justintime.jit.validators.ValidationDispatcher;
import jakarta.annotation.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Set;

public abstract class BaseController {

    @Autowired
    protected ValidationDispatcher validationDispatcher;

    protected <T> void validate(T dto, @Nullable Set<String> fieldsToValidate, @Nullable String restaurantCode) {
        validationDispatcher.process(dto, fieldsToValidate, restaurantCode);
    }

    protected <T> ResponseEntity<ApiResponse<T>> respond (T data, String message, HttpStatus status) {
        return ResponseEntity.status(status).body(new ApiResponse<>(status.value(), message, data));
    }

    protected <T> ResponseEntity<ApiResponse<T>> success(T data, String message) {
        return respond(data, message, HttpStatus.OK);
    }

    protected <T> ResponseEntity<ApiResponse<T>> success(T data) {
        return respond(data, "Success", HttpStatus.OK);
    }

    protected <T> ResponseEntity<ApiResponse<T>> error(String message, HttpStatus status) {
        return respond(null, message, status);
    }
}

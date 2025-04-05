package com.justintime.jit.controller;

import com.justintime.jit.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public abstract class BaseController {

    protected <T> ResponseEntity<ApiResponse<T>> respond (T data, String message, HttpStatus status) {
        ApiResponse<T> response = new ApiResponse<>(status.value(), message, data);
        return new ResponseEntity<ApiResponse<T>>(response, status);
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

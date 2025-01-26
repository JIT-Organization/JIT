package com.justintime.jit.exception;

public class ImageSizeLimitExceededException extends RuntimeException {
    public ImageSizeLimitExceededException(String message) {
        super(message);
    }
}
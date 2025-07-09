package com.justintime.jit.exception;

public class IllegalBatchStateException extends IllegalStateException {

    public IllegalBatchStateException() {
        super();
    }

    public IllegalBatchStateException(String message) {
        super(message);
    }

    public IllegalBatchStateException(String message, Throwable cause) {
        super(message, cause);
    }

    public IllegalBatchStateException(Throwable cause) {
        super(cause);
    }
}


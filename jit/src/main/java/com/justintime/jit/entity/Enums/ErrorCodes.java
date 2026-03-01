package com.justintime.jit.entity.Enums;

import lombok.Setter;

public enum ErrorCodes {

    EMPTY_CART(1000, "Cart cannot be empty"),
    INVALID_QUANTITY(1001, "Invalid item quantity"),
    ITEM_NOT_FOUND(1002, "Item not found"),
    ITEM_UNAVAILABLE(1003, "Item is currently unavailable"),
    PRICE_MISMATCH(1004, "Item price mismatch"),
    TABLE_NOT_AVAILABLE(1005, "Dining table is not available");

    private String message;
    private final int code;

    ErrorCodes(int code,String message) {
        this.code=code;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    void setMessage(String message) {
       this.message = message;
    }

    public int getCode() {
        return code;
    }
}

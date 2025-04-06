package com.justintime.jit.util;

import java.util.UUID;

public class CodeNumberGenerator {
    public static String generateCode(String entityName) {
        return switch (entityName) {
            case "restaurant"  -> "RES-" + generateUUID();
            case "reservation" -> "RSV-" + generateUUID();
            case "order"       -> "ORD-" + generateUUID();
            case "payment"     -> "PAY-" + generateUUID();
            default            -> "You must specify a valid Entity name";
        };
    }
    private static String generateUUID() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
    private CodeNumberGenerator(){
        //for preventing instantiation of this class
    }
}

package com.justintime.jit.util.sanitization;

import org.apache.commons.text.StringEscapeUtils;

import java.lang.reflect.Field;
import java.util.*;

public class SanitizationUtils {
    public static String sanitizeText(String input) {
        if (input == null) return null;
        String sanitized = input.trim();
        sanitized = sanitized.replaceAll("\\p{Cntrl}&&[^\n" + "\t]", "");
        sanitized = java.text.Normalizer.normalize(sanitized, java.text.Normalizer.Form.NFC);
        sanitized = StringEscapeUtils.escapeHtml4(sanitized);
        return sanitized;
    }

    public static boolean isBlank(String str) {
        return str == null || sanitizeText(str).isBlank();
    }

    /**
     * Public entry point for sanitizing an object.
     * It creates a tracking set to prevent infinite loops.
     */
    public static void sanitizeObject(Object obj) {
        // Use IdentityHashMap to track objects by reference, not .equals()
        sanitizeObject(obj, Collections.newSetFromMap(new IdentityHashMap<>()));
    }

    /**
     * Private worker method that recursively sanitizes and tracks visited objects.
     */
    private static void sanitizeObject(Object obj, Set<Object> visited) {
        if (obj == null) return;

        // ** THIS IS THE FIX **
        // If we have already sanitized this exact object instance, stop.
        if (visited.contains(obj)) {
            return;
        }

        // Add the object to our "seen" list before processing
        visited.add(obj);

        Class<?> clazz = obj.getClass();

        // Base case for Java libraries and primitives
        if (clazz.isPrimitive() || clazz.getName().startsWith("java.")) {
            return;
        }

        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            try {
                Object value = field.get(obj);

                if (value instanceof String) {
                    field.set(obj, sanitizeText((String) value));
                } else if (value instanceof Collection<?> collection) {
                    for (Object item : collection) {
                        // Pass the tracking set down in the recursive call
                        sanitizeObject(item, visited);
                    }
                } else {
                    // Pass the tracking set down in the recursive call
                    sanitizeObject(value, visited);
                }

            } catch (IllegalAccessException e) {
                throw new RuntimeException("Sanitization failed for field: " + field.getName(), e);
            }
        }
    }
}
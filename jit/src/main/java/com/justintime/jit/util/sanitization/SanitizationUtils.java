package com.justintime.jit.util.sanitization;

import org.apache.commons.text.StringEscapeUtils;

import java.lang.reflect.Field;
import java.util.Collection;

public class SanitizationUtils {
    public static String sanitizeText(String input) {
        if (input == null) return null;

        // Trim
        String sanitized = input.trim();

        // Remove control characters (non-printable ASCII)
        sanitized = sanitized.replaceAll("\\p{Cntrl}&&[^\n" + "\t]", "");

        // Normalize Unicode (optional: use NFC/NFD forms)
        sanitized = java.text.Normalizer.normalize(sanitized, java.text.Normalizer.Form.NFC);

        // Escape HTML to prevent XSS
        sanitized = StringEscapeUtils.escapeHtml4(sanitized);

        return sanitized;
    }

    public static boolean isBlank(String str) {
        return str == null || sanitizeText(str).isBlank();
    }

    public static void sanitizeObject(Object obj) {
        if (obj == null) return;

        Class<?> clazz = obj.getClass();
        if (clazz.getName().startsWith("java")) return;

        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            try {
                Object value = field.get(obj);

                if (value instanceof String) {
                    field.set(obj, sanitizeText((String) value));
                } else if (value instanceof Collection<?> collection) {
                    for (Object item : collection) {
                        sanitizeObject(item);
                    }
                } else if (!field.getType().isPrimitive()) {
                    sanitizeObject(value);
                }

            } catch (IllegalAccessException e) {
                throw new RuntimeException("Sanitization failed for field: " + field.getName(), e);
            }
        }
    }
}

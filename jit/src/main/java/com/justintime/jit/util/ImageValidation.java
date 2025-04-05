package com.justintime.jit.util;

import com.justintime.jit.exception.ImageSizeLimitExceededException;

public class ImageValidation {

    // Helper method to calculate padding length for base64 string
    private static int countPadding(String encodedData) {
        if(encodedData.endsWith("==")) {
            return 2;
        } else if (encodedData.endsWith("=")) {
            return 1;
        }
        return 0;
    }

    // Method to validate image size based on base64 encoded data
    public static void validateImageSize(String base64Data, long maxSize) throws ImageSizeLimitExceededException {
        // Extract base64 part if the data URL prefix is included (e.g., data:image/png;base64,...)
        String encodedData = base64Data.contains(",")
                ? base64Data.split(",")[1]
                : base64Data;

        // Calculate padding (if any) and determine the actual image size
        int padding = countPadding(encodedData);
        long imageSize = (encodedData.length() * 3L / 4) - padding;  // Base64 size formula

        // If the image exceeds the max size, throw an exception
        if(imageSize > maxSize) {
            // Throw a custom exception with a descriptive message
            throw new ImageSizeLimitExceededException("Please upload an image with size less than "
                    + (maxSize / 1024 / 1024) + " MB. The uploaded image size is "
                    + (imageSize / 1024 / 1024) + " MB.");
        }
    }
}
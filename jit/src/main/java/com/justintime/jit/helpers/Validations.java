package com.justintime.jit.helpers;

import com.justintime.jit.exception.ImageSizeLimitExceededException;

public class Validations {

    private static int countPadding(String encodedData) {
        if(encodedData.endsWith("==")) {
            return 2;
        } else if (encodedData.endsWith("=")) {
            return 1;
        }
        return 0;
    }

    public static void validateImageSize(String base64Data, long maxSize) throws ImageSizeLimitExceededException {
        String encodedData = base64Data.contains(",")
                ? base64Data.split(",")[1]
                : base64Data;

        int padding = countPadding(encodedData);
        long imageSize = (encodedData.length() * 3L / 4) - padding;
        if(imageSize > maxSize) {
            throw new ImageSizeLimitExceededException("Please upload an image with size less than " + (maxSize / 1024 / 1024) + " MB");
        }
    }
}

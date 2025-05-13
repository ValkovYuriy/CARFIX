package yuriy.dev.carfixbackend.util;

import java.util.Base64;

public class ImageConverter {

    public static String toBase64(byte[] imageBytes) {
        if (imageBytes == null) {
            return null;
        }
        return Base64.getEncoder().encodeToString(imageBytes);
    }

    public static byte[] toByteArray(String base64String) {
        if (base64String == null || base64String.isEmpty()) {
            return null;
        }
        return Base64.getDecoder().decode(base64String);
    }
}

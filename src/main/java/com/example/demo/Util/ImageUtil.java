package com.example.demo.Util;

import org.springframework.stereotype.Component;

import java.util.Base64;

@Component
public class ImageUtil {
    public static byte[] decodeBase64Image(String base64Image) {
        return Base64.getDecoder().decode(base64Image);
    }
}

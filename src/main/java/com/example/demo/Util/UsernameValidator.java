package com.example.demo.Util;

import org.springframework.stereotype.Component;

@Component
public class UsernameValidator {
    private static final Integer USERNAME_MIN_LENGTH = 1;
    private static final Integer USERNAME_MAX_LENGTH = 30;

    public static boolean ValidUsername(String username) {
        boolean hasWhitespace = username.contains(" ");
        boolean hasSpecialCharacters = !username.matches("[A-Za-z0-9 ]*");
        return !hasWhitespace && !hasSpecialCharacters;
    }

    public static boolean UsernameLength(String username) {
        return username.length() >= USERNAME_MIN_LENGTH && username.length() <= USERNAME_MAX_LENGTH;
    }
}

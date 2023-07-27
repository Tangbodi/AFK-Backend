package com.example.demo.Validator;

import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class PasswordValidation {
    private static final String SPECIAL_CHARACTERS_REGEX = "^(?=.*?[A-Z])(?=(.*[a-z]){1,})(?=(.*[\\d]){1,})(?=(.*[\\W_]){1,})(?!.*\\s).{8,}$";

    private static final Integer PASSWORD_MIN_LENGTH = 8;
    private static final Integer PASSWORD_MAX_LENGTH = 63;

    public static boolean ValidPassword(String password){
        Pattern pattern= Pattern.compile(SPECIAL_CHARACTERS_REGEX);
        Matcher matcher = pattern.matcher(password);
        boolean isPassword = matcher.find();
        return isPassword;
    }
}

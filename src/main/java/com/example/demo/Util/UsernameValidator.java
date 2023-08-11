package com.example.demo.Util;

import com.example.demo.Annotation.ValidUsername;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

@Component
public class UsernameValidator implements ConstraintValidator<ValidUsername, String> {
    private static final String SPECIAL_CHARACTERS_REGEX = ".*[^A-Za-z0-9].*";
    private static final Pattern pattern = Pattern.compile(SPECIAL_CHARACTERS_REGEX );

//    private static final Integer USERNAME_MIN_LENGTH = 1;
//    private static final Integer USERNAME_MAX_LENGTH = 30;

//    public static boolean ValidUsername(String username) {
//        boolean hasWhitespace = username.contains(" ");
//        boolean hasSpecialCharacters = !username.matches("[A-Za-z0-9 ]*");
//        return !hasWhitespace && !hasSpecialCharacters;
//    }
//
//    public static boolean UsernameLength(String username) {
//        return username.length() >= USERNAME_MIN_LENGTH && username.length() <= USERNAME_MAX_LENGTH;
//    }

    @Override
    public boolean isValid(String username, ConstraintValidatorContext constraintValidatorContext) {
//        if (username.length() < 1 || username.length() > 30) {
//            context.disableDefaultConstraintViolation();
//            context.buildConstraintViolationWithTemplate("Username length must be between 1 and 30 characters.")
//                    .addConstraintViolation();
//            return false;
//        }
//
//        if (!pattern.matcher(username).matches()) {
//            context.disableDefaultConstraintViolation();
//            context.buildConstraintViolationWithTemplate("Username can only contain alphanumeric characters.")
//                    .addConstraintViolation();
//            return false;
//        }
        return !username.contains(" ") && !username.matches(SPECIAL_CHARACTERS_REGEX);
    }
}

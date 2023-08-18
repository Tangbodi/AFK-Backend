package com.example.demo.Util;

import com.example.demo.Annotation.ValidUsername;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

@Component
public class UsernameValidator implements ConstraintValidator<ValidUsername, String> {
    private static final String SPECIAL_CHARACTERS_REGEX = ".*[^A-Za-z0-9].*";
    private static final Pattern pattern = Pattern.compile(SPECIAL_CHARACTERS_REGEX);


    @Override
    public boolean isValid(String username, ConstraintValidatorContext constraintValidatorContext) {
        return !username.contains(" ") && !username.matches(SPECIAL_CHARACTERS_REGEX);
    }
}

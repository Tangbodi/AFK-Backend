package com.example.demo.Util;

import com.example.demo.Annotation.ValidPassword;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

@Component
public class PasswordValidator implements ConstraintValidator<ValidPassword, String> {
    private static final String SPECIAL_CHARACTERS_REGEX = "^(?=.*?[A-Z])(?=(.*[a-z]){1,})(?=(.*[\\d]){1,})(?=(.*[\\W_]){1,})(?!.*\\s).{8,}$";
    private static final Pattern pattern = Pattern.compile(SPECIAL_CHARACTERS_REGEX);

    @Override
    public boolean isValid(String password, ConstraintValidatorContext constraintValidatorContext) {

        return !password.contains(" ") && pattern.matcher(password).matches();
    }
}

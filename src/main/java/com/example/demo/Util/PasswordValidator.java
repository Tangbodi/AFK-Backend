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
//    private static final Integer PASSWORD_MIN_LENGTH = 8;
//    private static final Integer PASSWORD_MAX_LENGTH = 30;


//    public static boolean isValidPassword(String password) {
//        Pattern pattern = Pattern.compile(SPECIAL_CHARACTERS_REGEX);
//        Matcher matcher = pattern.matcher(password);
//        boolean isPassword = matcher.find();
//        return isPassword;
//    }

//    public static boolean PasswordLength(String password) {
//        return password.length() >= PASSWORD_MIN_LENGTH && password.length() <= PASSWORD_MAX_LENGTH;
//    }

    @Override
    public boolean isValid(String password, ConstraintValidatorContext constraintValidatorContext) {

        return !password.contains(" ") && pattern.matcher(password).matches();
    }
}

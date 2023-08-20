package com.example.demo.Util;

import com.example.demo.Annotation.ValidPhone;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class PhoneValidator implements ConstraintValidator<ValidPhone, String> {
    private static final Pattern pattern = Pattern.compile("^\\d{10}$");

    @Override
    public boolean isValid(String phone, ConstraintValidatorContext constraintValidatorContext) {
        Matcher matcher = pattern.matcher(phone);
        return (matcher.matches());
    }
}

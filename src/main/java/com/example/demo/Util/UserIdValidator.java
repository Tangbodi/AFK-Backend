package com.example.demo.Util;

import com.example.demo.Annotation.ValidUserId;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Component
public class UserIdValidator implements ConstraintValidator<ValidUserId, String> {
    private static final Integer USER_LENGTH = 32;
    @Override
    public boolean isValid(String userId, ConstraintValidatorContext constraintValidatorContext) {
        if(userId.length()==USER_LENGTH){
            return true;
        }else{
            return false;
        }
    }
}

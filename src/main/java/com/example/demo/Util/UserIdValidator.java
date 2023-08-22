package com.example.demo.Util;

import com.example.demo.Annotation.ValidUserId;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Component
public class UserIdValidator implements ConstraintValidator<ValidUserId, Long> {
    private static final Integer USER_LENGTH = 19;
    @Override
    public boolean isValid(Long userId, ConstraintValidatorContext constraintValidatorContext) {
        if(userId.toString().length()==USER_LENGTH){
            return true;
        }else{
            return false;
        }
    }
}

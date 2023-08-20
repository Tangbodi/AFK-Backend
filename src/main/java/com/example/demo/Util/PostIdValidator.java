package com.example.demo.Util;

import com.example.demo.Annotation.ValidPostId;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.UUID;

@Component
public class PostIdValidator implements ConstraintValidator<ValidPostId, String> {
    private static final Integer POST_LENGTH = 32;

    @Override
    public boolean isValid(String postId, ConstraintValidatorContext constraintValidatorContext) {
        if(postId.length()==POST_LENGTH){
            return true;
        }else{
            return false;
        }
    }
}

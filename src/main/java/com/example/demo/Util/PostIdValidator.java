package com.example.demo.Util;

import com.example.demo.Annotation.ValidPostId;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.UUID;

@Component
public class PostIdValidator implements ConstraintValidator<ValidPostId, Long> {
    private static final Integer POST_LENGTH = 19;

    @Override
    public boolean isValid(Long postId, ConstraintValidatorContext constraintValidatorContext) {
        if(postId.toString().length()==POST_LENGTH){
            return true;
        }else{
            return false;
        }
    }
}

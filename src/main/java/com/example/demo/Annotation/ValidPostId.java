package com.example.demo.Annotation;

import com.example.demo.Util.PasswordValidator;
import com.example.demo.Util.PostIdValidator;

import javax.validation.Constraint;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PostIdValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPostId {
    String message() default "Post not found";
    Class<?>[] groups() default {};
    Class<?>[] payload() default {};
}

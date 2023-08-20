package com.example.demo.Annotation;

import com.example.demo.Util.GameIdValidator;
import com.example.demo.Util.PasswordValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = GameIdValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidGameId {
    String message() default "Game not found";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

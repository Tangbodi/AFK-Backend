package com.example.demo.Annotation;

import com.example.demo.Util.GenreIdValidator;
import com.example.demo.Util.PasswordValidator;

import javax.validation.Constraint;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = GenreIdValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidGenreId {
    String message() default "Game not found";
    Class<?>[] groups() default {};
    Class<?>[] payload() default {};
}

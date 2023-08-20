package com.example.demo.Annotation;

import com.example.demo.Util.PasswordValidator;
import com.example.demo.Util.PhoneValidator;

import javax.validation.Constraint;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PhoneValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPhone {
    String message() default "Invalid Phone";
    Class<?>[] groups() default {};
    Class<?>[] payload() default {};
}

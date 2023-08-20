package com.example.demo.Annotation;

import com.example.demo.Util.ForumSearchKeywordValidator;
import com.example.demo.Util.UserIdValidator;

import javax.validation.Constraint;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = UserIdValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ValidUserId {
    String message() default "User not found";
    Class<?>[] groups() default {};
    Class<?>[] payload() default {};
}

package com.example.demo.Annotation;

import com.example.demo.Util.ForumSearchKeywordValidator;
import com.example.demo.Util.PasswordValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ForumSearchKeywordValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ValidForumKeyword {
    String message() default "Invalid Keyword";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}



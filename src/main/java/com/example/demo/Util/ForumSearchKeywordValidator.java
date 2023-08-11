package com.example.demo.Util;

import com.example.demo.Annotation.ValidForumKeyword;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

@Component
public class ForumSearchKeywordValidator implements ConstraintValidator<ValidForumKeyword, String> {
    private static final String FORUM_SEARCH_PATTERN = ".*[^A-Za-z0-9].*";
    private static final Pattern pattern = Pattern.compile(FORUM_SEARCH_PATTERN);
    @Override
    public boolean isValid(String keyword, ConstraintValidatorContext constraintValidatorContext) {
        return !keyword.contains(" ") && !pattern.matcher(keyword).matches();
    }
}

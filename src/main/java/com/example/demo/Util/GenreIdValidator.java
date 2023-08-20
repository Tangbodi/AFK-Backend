package com.example.demo.Util;

import com.example.demo.Annotation.ValidGenreId;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Component
public class GenreIdValidator implements ConstraintValidator<ValidGenreId,Byte> {
    private static final Byte GENRE_MIN = 1;
    private static final Byte GENRE_MAX = 20;

    @Override
    public boolean isValid(Byte genreId, ConstraintValidatorContext constraintValidatorContext) {
        if (genreId < GENRE_MIN || genreId > GENRE_MAX) {
            return false;
        } else {
            return true;
        }
    }
}

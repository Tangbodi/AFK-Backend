package com.example.demo.Util;

import com.example.demo.Annotation.ValidGameId;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Component
public class GameIdValidator implements ConstraintValidator<ValidGameId, Short> {
    private static final Short GAME_MIN = 102;
    private static final Short GAME_MAX = 32766;

    @Override
    public boolean isValid(Short gameId, ConstraintValidatorContext constraintValidatorContext) {
        if (gameId < GAME_MIN || gameId > GAME_MAX || gameId % 3 != 0) {
            return false;
        } else {
            return true;
        }
    }
}

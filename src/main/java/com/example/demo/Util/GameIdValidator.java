package com.example.demo.Util;

public class GameIdValidator {
    private static final Short GAME_MIN = 102;
    private static final Short GAME_MAX = 32766;

    public static boolean CheckGameId(Short gameId) {
        if (gameId < GAME_MIN || gameId > GAME_MAX || gameId % 3 != 0) {
            return false;
        } else {
            return true;
        }
    }
}

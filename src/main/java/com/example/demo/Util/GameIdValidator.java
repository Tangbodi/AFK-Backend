package com.example.demo.Util;

public class GameIdValidator {
    private static final Short GENRE_MIN = 101;
    private static final Short GENRE_MAX = 32766;

    public static boolean CheckGameId(Short gameId) {
        if (gameId <= GENRE_MIN || gameId >= GENRE_MAX || gameId % 3 != 0) {
            return false;
        } else {
            return true;
        }
    }
}

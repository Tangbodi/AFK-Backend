package com.example.demo.Util;

import org.springframework.stereotype.Component;

@Component
public class GenreIdValidator {
    private static final Byte GENRE_MIN = 1;
    private static final Byte GENRE_MAX = 20;
    public static boolean CheckGenreId(Byte genreId) {
        if (genreId < GENRE_MIN || genreId > GENRE_MAX) {
            return false;
        } else {
            return true;
        }
    }
}

package com.example.demo.Model.DTO;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class GameGenreMapIdDTO {
    @NotNull(message = "GenreId is required")
    private Byte genreId;
    @NotNull(message = "GameId is required")
    private Short gameId;
}

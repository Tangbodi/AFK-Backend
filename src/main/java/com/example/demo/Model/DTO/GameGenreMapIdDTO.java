package com.example.demo.Model.DTO;

import com.example.demo.Annotation.ValidGameId;
import com.example.demo.Annotation.ValidGenreId;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class GameGenreMapIdDTO{

    private Long userId;
    @NotNull(message = "GenreId is required")
    @ValidGenreId
    private Byte genreId;
    @NotNull(message = "GameId is required")
    @ValidGameId
    private Short gameId;

}

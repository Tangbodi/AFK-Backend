package com.example.demo.Model.DTO;

import com.example.demo.Annotation.ValidGameId;
import com.example.demo.Annotation.ValidGenreId;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.Instant;

@Data
public class PostDTO {
    @NotNull(message = "GenreId is required")
    @ValidGenreId
    private Byte genreId;
    @NotNull(message = "GameId is required")
    @ValidGameId
    private Short gameId;
    private Long userId;
    private Long postId;
    @NotBlank(message = "Title is required")
    @Length(max = 255, message = "Title length not eligible")
    private String title;
    @NotBlank(message = "Content is required")
    @Length(max = 4095, message = "Text length not eligible")
    private String textRender;

    private Long ipvFour;
    private String ipvSix;
    private Instant createdAt;

}

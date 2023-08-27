package com.example.demo.Model.DTO;

import com.example.demo.Annotation.ValidGameId;
import com.example.demo.Annotation.ValidGenreId;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.Instant;
import java.util.List;

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
    @Size(max = 9, message = "You can upload up to 9 images")
    private List<String> postImageNameList;
    private Long ipvFour;
    private String ipvSix;
    private Instant createdAt;

}

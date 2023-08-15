package com.example.demo.Model.DTO;

import com.example.demo.Model.Entity.PostImage;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.List;

@Data
public class PostDTO {
    @NotNull(message = "GenreId is required")
    private Byte genreId;
    @NotNull(message = "GameId is required")
    private Short gameId;
    private String userId;
    private String postId;
    @NotBlank(message = "Title is required")
    @Length(max = 255, message = "Title length not eligible")
    private String title;
    @NotBlank(message = "Content is required")
    @Length(max = 4095, message = "Text length not eligible")
    private String textRender;
    private List<PostImageDTO> images;
    private Long ipvFour;
    private String ipvSix;
    private Instant createdAt;

}

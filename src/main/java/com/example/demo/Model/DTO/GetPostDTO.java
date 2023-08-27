package com.example.demo.Model.DTO;

import com.example.demo.Annotation.ValidGameId;
import com.example.demo.Annotation.ValidGenreId;
import com.example.demo.Annotation.ValidPostId;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class GetPostDTO {
    @NotNull(message = "postId is required")
    @ValidPostId
    private Long postId;
    @NotNull
    @ValidGameId
    private Short gameId;
    @NotNull
    @ValidGenreId
    private Byte genreId;
}

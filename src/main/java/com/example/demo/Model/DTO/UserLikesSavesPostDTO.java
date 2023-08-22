package com.example.demo.Model.DTO;

import com.example.demo.Annotation.ValidPostId;
import com.example.demo.Annotation.ValidUserId;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class UserLikesSavesPostDTO {
    @NotNull(message = "postId is required")
    private Long postId;

    private Long userId;
    private String type;
}

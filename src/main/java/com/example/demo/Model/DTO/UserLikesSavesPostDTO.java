package com.example.demo.Model.DTO;

import com.example.demo.Annotation.ValidPostId;
import com.example.demo.Annotation.ValidUserId;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class UserLikesSavesPostDTO {
    @NotBlank(message = "postId is required")
    @ValidPostId
    private String postId;
    @NotBlank(message = "userId is required")
    @ValidUserId
    private String userId;
    private String type;
}

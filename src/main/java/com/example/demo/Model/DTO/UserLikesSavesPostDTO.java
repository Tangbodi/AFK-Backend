package com.example.demo.Model.DTO;

import com.example.demo.Annotation.ValidPostId;
import com.example.demo.Annotation.ValidUserId;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class UserLikesSavesPostDTO {
    @NotNull(message = "postId is required")
    @ValidPostId
    private Long postId;
    private Long userId;
    @NotBlank(message = "Type is required")
    @Length(min = 4, max = 4, message = "Invalid type")
    private String type;
}

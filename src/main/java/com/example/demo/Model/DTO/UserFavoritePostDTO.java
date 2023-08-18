package com.example.demo.Model.DTO;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class UserFavoritePostDTO {
    @NotBlank(message = "postId is required")
    private String postId;
    private String userId;
    private String type;
}

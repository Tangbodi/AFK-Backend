package com.example.demo.Model.DTO;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.time.Instant;

@Data
public class PostDTO {
    private String userId;
    private String postId;
    private String username;
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

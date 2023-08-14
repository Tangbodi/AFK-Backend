package com.example.demo.Model.DTO;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.time.Instant;

@Data
public class CommentDTO {
    private String commentId;
    @NotBlank(message = "PostId is required")
    private String postId;
    @NotBlank(message = "Content is required")
    private String content;
    private String fromUid;
    private Long ipvFour;
    private String ipvSix;
    private Instant createdAt;

}

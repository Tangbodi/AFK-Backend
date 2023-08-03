package com.example.demo.Model.DTO;

import lombok.Data;

@Data
public class CommentDTO {
    private String commentId;
    private String postId;
    private Byte genreId;
    private String content;
    private String fromUid;
    private Long ipvFour;
    private String ipvSix;
    private String createdAt;

}

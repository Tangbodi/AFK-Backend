package com.example.demo.Model.VO;

import lombok.Data;

import java.time.Instant;

@Data
public class CommentSavedVO {
    private String commentId;
    private String postId;
    private Instant createdAt;
}

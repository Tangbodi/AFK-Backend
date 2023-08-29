package com.example.demo.Model.VO;

import lombok.Data;

import java.time.Instant;

@Data
public class NewestCommentVO {
    private String postId;
    private String content;
    private String gameName;
    private Instant createdAt;
}

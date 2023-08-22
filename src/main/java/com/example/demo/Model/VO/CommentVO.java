package com.example.demo.Model.VO;

import lombok.Data;

import java.time.Instant;

@Data
public class CommentVO {
    private Long commentId;
    private Long postId;
    private Instant createdAt;
}

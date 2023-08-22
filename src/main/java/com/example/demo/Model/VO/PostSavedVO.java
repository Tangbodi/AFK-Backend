package com.example.demo.Model.VO;

import lombok.Data;

import java.time.Instant;

@Data
public class PostSavedVO {
    private Long postId;
    private Instant createdAt;
}

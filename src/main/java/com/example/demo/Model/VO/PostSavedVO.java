package com.example.demo.Model.VO;

import lombok.Data;

import java.time.Instant;

@Data
public class PostSavedVO {
    private String postId;
    private Instant createdAt;
}

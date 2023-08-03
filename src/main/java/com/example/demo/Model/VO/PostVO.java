package com.example.demo.Model.VO;

import lombok.Data;

import java.time.Instant;

@Data
public class PostVO {
    private String userId;
    private String postId;
    private Byte genreId;
    private Instant createdAt;
}

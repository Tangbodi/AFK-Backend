package com.example.demo.Model.VO;

import lombok.Data;

import java.time.Instant;

@Data
public class PostInfoVO {
    private Long postId;
    private String title;
    private String username;
    private Integer view;
    private Integer comment;
    private Integer like;
    private Integer save;
    private Instant CreatedAt;
}

package com.example.demo.Model.VO;

import lombok.Data;

import java.time.Instant;

@Data
public class LatestPostVO {
    private String postId;
    private String title;
    private String gameName;
    private Instant createdAt;

}

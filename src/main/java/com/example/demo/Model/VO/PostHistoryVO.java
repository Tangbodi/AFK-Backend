package com.example.demo.Model.VO;

import lombok.Data;

import java.time.Instant;

@Data
public class PostHistoryVO {
    private String postId;
    private String title;
    private Integer view;
    private Integer comment;
    private Integer like;
    private Integer favorite;
    private Instant created_at;
}

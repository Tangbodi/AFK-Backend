package com.example.demo.Model.VO;

import lombok.Data;

import java.time.Instant;

@Data
public class ShowSavedPostVO {
    private String postId;
    private String title;
    private Integer view;
    private Integer commentReply;
    private Integer like;
    private Instant createdAt;
}

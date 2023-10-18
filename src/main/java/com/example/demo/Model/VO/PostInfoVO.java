package com.example.demo.Model.VO;

import lombok.Data;

import java.time.Instant;

@Data
public class PostInfoVO {
    private String postId;
    private String title;
    private String username;
    private String avatarUrl;
    private String view;
    private String reply;
    private String like;
    private String save;
    private String CreatedAt;
}

package com.example.demo.Model.VO;

import lombok.Data;

import java.sql.Timestamp;
import java.time.Instant;

@Data
public class ShowCommentVO{
    private String commentId;
    private String postId;
    private String fromUid;
    private String username;
    private Object avatarUrl;
    private String content;
    private Boolean likeStatus;
    private Instant createdAt;


}

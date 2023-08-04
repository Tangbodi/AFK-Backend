package com.example.demo.Model.VO;

import lombok.Data;

import java.time.Instant;

@Data
public class ReplyVO {
    private String replyId;
    private String commentId;
    private String parentReplyId;
    private String toUid;
    private Instant createdAt;
}

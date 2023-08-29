package com.example.demo.Model.VO;

import lombok.Data;

import java.time.Instant;

@Data
public class ReplySavedVO {
    private String replyId;
    private String commentId;
    private String toReplyId;
    private String toUid;
    private Instant createdAt;
}

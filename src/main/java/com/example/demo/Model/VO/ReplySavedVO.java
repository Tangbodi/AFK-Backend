package com.example.demo.Model.VO;

import lombok.Data;

import java.time.Instant;

@Data
public class ReplySavedVO {
    private Long replyId;
    private Long commentId;
    private Long toReplyId;
    private Long toUid;
    private Instant createdAt;
}

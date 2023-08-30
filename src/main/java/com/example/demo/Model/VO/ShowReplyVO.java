package com.example.demo.Model.VO;

import lombok.Data;

import java.sql.Timestamp;
import java.time.Instant;

@Data
public class ShowReplyVO {
    private String replyId;
    private String commentId;
    private String toReplyId;
    private String fromUid;
    private String toUid;
    private String fromAvatarURL;
    private String fromUsername;
    private String toUsername;
    private String content;
    private Integer likeStatus;
    private Instant createdAt;

}

package com.example.demo.Model.VO;

import lombok.Data;

import java.sql.Timestamp;
import java.time.Instant;

@Data
public class ShowReplyVO {
    private String replyId;
    private String commentId;
    private Object toReplyId;
    private String fromUid;
    private String toUid;
    private Object fromAvatarURL;
    private String fromUsername;
    private String toUsername;
    private String content;
    private Boolean likeStatus;
    private Instant createdAt;




}

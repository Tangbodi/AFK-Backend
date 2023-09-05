package com.example.demo.Model.VO;

import lombok.Data;

import java.time.Instant;

@Data
public class MessageVO {
    private String commentReplyId;
    private String content;
    private String fromUid;
    private String fromUsername;
    private Instant createdAt;
}

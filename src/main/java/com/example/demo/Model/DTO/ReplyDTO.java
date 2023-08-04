package com.example.demo.Model.DTO;

import lombok.Data;
import org.springframework.data.relational.core.sql.In;

import java.time.Instant;

@Data
public class ReplyDTO {
    private String replyId;
    private String commentId;
    private String parentReplyId;
    private Boolean replyType;
    private String content;
    private String fromUid;
    private String toUid;
    private Long ipvFour;
    private String ipvSix;
    private Instant createdAt;
}

package com.example.demo.Model.VO;

import lombok.Data;

import java.io.Serializable;
import java.time.Instant;

@Data
public class MessageVO implements Serializable {
    private String messageId;
    private String objectId;
    private String content;
    private String fromUid;
    private String toUid;
    private String fromUsername;
    private String fromAvatarUrl;
    private String typeId;
    private String createdAt;
}

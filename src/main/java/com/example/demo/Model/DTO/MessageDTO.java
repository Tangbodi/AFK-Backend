package com.example.demo.Model.DTO;

import lombok.Data;

import java.io.Serializable;
import java.time.Instant;

@Data
public class MessageDTO implements Serializable {
    private Long commentReplyId;
    private String content;
    private Long fromUid;
    private Long toUid;
    private Integer typeId;
    private Instant createdAt;

}

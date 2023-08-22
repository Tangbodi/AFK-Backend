package com.example.demo.Model.VO;

import lombok.Data;

@Data
public class MessageVO {
    private Long crId;
    private String content;
    private Long fromUid;
    private String fromUsername;

}

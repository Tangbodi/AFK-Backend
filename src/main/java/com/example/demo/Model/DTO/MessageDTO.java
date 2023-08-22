package com.example.demo.Model.DTO;

import lombok.Data;

@Data
public class MessageDTO {
    private Long MessageId;
    private Long fromUid;
    private Long toUid;
    private Boolean readStatus;

}

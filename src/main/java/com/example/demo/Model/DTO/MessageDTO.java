package com.example.demo.Model.DTO;

import lombok.Data;

@Data
public class MessageDTO {
    private Integer MessageId;
    private String fromUid;
    private String toUid;
    private Boolean readStatus;

}

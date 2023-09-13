package com.example.demo.Model.DTO;

import lombok.Data;

import java.time.Instant;

@Data
public class ObjectUserDTO {
    private Long objectId;
    private Long userId;
    private Integer status;
}

package com.example.demo.Model.DTO;

import lombok.Data;

import java.time.Instant;

@Data
public class UserInfoDTO {
    private Long userId;
    private String username;
    private String email;
    private String phone;
    private String avatarUrl;
    private Instant createdAt;
    private Instant modifiedAt;
}

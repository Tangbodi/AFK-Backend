package com.example.demo.Model.VO;

import lombok.Data;

import java.time.Instant;


@Data
public class UserInfoVO {

    private String username;
    private String email;
    private String avatarUrl;
    private Instant createdAt;
    private Instant modifiedAt;
}

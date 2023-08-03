package com.example.demo.Model.VO;

import lombok.Data;

import java.time.Instant;


@Data
public class UserInfoVO {

    private String userId;
    private String username;
    private String email;
    private String avatar_url;
    private Instant createdAt;
    private Instant modifiedAt;
}

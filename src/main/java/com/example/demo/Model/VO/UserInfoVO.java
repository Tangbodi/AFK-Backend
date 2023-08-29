package com.example.demo.Model.VO;

import lombok.Data;

import java.time.Instant;


@Data
public class UserInfoVO {

    private Long LongUid;
    private String userId;
    private String username;
    private String email;
    private String avatarUrl;
    private String JSESSIONID;
    private Instant createdAt;
    private Instant modifiedAt;
}

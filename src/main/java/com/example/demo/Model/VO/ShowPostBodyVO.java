package com.example.demo.Model.VO;

import lombok.Data;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
public class ShowPostBodyVO {
    private Long postId;
    private Long userId;
    private String userName;
    private String title;
    private String textRender;
    private List<String> imageURL;
    private Instant createdAt;
}

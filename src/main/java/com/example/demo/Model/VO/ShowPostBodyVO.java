package com.example.demo.Model.VO;

import lombok.Data;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
public class ShowPostBodyVO {
    private String postId;
    private String userId;
    private String userName;
    private String title;
    private String textRender;
    private List<String> imageURL = new ArrayList<>();
    private Instant createdAt;
}

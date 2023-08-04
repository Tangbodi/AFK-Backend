package com.example.demo.Model.VO;

import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
public class ShowPostVO {
    private String title;
    private String textRender;
    private String userName;
    private List<String> imageURL;
    private Instant createdAt;
}

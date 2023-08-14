package com.example.demo.Model.VO;

import lombok.Data;

import java.time.Instant;

@Data
public class NewsVO {
    private String newsId;
    private String title;
    private String link;
    private String description;
    private String mediaContentUrl;
    private String pubDate;
}

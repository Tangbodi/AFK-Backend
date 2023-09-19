package com.example.demo.Model.VO;

import lombok.Data;

import java.time.Instant;

@Data
public class NewsVO {
    private String newsId;
    private Byte genreId;
    private Short gameId;
    private String gameName;
    private String gameIconUrl;
    private String source;
    private String title;
    private String description;
    private String mediaContentUrl;
    private String content;
    private String pubDate;
}

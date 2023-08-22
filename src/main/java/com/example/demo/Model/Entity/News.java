package com.example.demo.Model.Entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "news")
public class News {
    @Id
    @Size(max = 15)
    @Column(name = "news_id", nullable = false, length = 15)
    private String id;

    @Size(max = 31)
    @NotNull
    @Column(name = "source", nullable = false, length = 31)
    private String source;

    @Size(max = 255)
    @NotNull
    @Column(name = "title", nullable = false)
    private String title;

    @Size(max = 255)
    @NotNull
    @Column(name = "link", nullable = false)
    private String link;

    @NotNull
    @Lob
    @Column(name = "description", nullable = false)
    private String description;

    @Size(max = 255)
    @NotNull
    @Column(name = "media_content_url", nullable = false)
    private String mediaContentUrl;

    @Size(max = 63)
    @NotNull
    @Column(name = "pub_date", nullable = false, length = 63)
    private String pubDate;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @NotNull
    @Column(name = "modified_at", nullable = false)
    private Instant modifiedAt;

}
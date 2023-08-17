package com.example.demo.Model.Entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.InstantDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.InstantSerializer;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
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
    @Column(name = "source", length = 31)
    private String source;

    @Size(max = 255)
    @Column(name = "title")
    private String title;

    @Size(max = 255)
    @Column(name = "link")
    private String link;

    @Size(max = 8191)
    @Column(name = "description", length = 8191)
    private String description;

    @Size(max = 255)
    @Column(name = "media_content_url")
    private String mediaContentUrl;
    @Size(max = 63)
    @Column(name = "pub_date")
    private String pubDate;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "modified_at")
    private Instant modifiedAt;

}
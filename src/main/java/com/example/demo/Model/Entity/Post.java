package com.example.demo.Model.Entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "posts")
public class Post {
    @Id
    @Column(name = "post_id", nullable = false, length = 36)
    private String id;

    @Column(name = "tag_id", nullable = false)
    private Byte tagId;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "text_render", nullable = false, length = 4095)
    private String textRender;

    @Column(name = "ipv_four", columnDefinition = "INT UNSIGNED")
    private Long ipvFour;

    @Column(name = "ipv_six", length = 16)
    private String ipvSix;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "modified_at", nullable = false)
    private Instant modifiedAt;

    public String getPostId() {
        return id;
    }

    public void setPostId(String postId) {
        this.id = postId;
    }

    public Byte getTagId() {
        return tagId;
    }

    public void setTagId(Byte tagId) {
        this.tagId = tagId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTextRender() {
        return textRender;
    }

    public void setTextRender(String textRender) {
        this.textRender = textRender;
    }

    public Long getIpvFour() {
        return ipvFour;
    }

    public void setIpvFour(Long ipvFour) {
        this.ipvFour = ipvFour;
    }

    public String getIpvSix() {
        return ipvSix;
    }

    public void setIpvSix(String ipvSix) {
        this.ipvSix = ipvSix;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getModifiedAt() {
        return modifiedAt;
    }

    public void setModifiedAt(Instant modifiedAt) {
        this.modifiedAt = modifiedAt;
    }

}
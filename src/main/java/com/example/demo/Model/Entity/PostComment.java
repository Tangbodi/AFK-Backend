package com.example.demo.Model.Entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "post_comments")
public class PostComment {
    @Id
    @Column(name = "comment_id", nullable = false, length = 36)
    private String id;

    @Column(name = "post_id", nullable = false, length = 36)
    private String postId;

    @Column(name = "tag_id", nullable = false)
    private Byte tagId;

    @Column(name = "content", nullable = false, length = 4095)
    private String content;

    @Column(name = "from_uid", nullable = false, length = 36)
    private String fromUid;

    @Column(name = "ipv_four", columnDefinition = "INT UNSIGNED")
    private Long ipvFour;

    @Column(name = "ipv_six", length = 16)
    private String ipvSix;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "modified_at", nullable = false)
    private Instant modifiedAt;

    public String getCommentId() {
        return id;
    }

    public void setCommentId(String commentId) {
        this.id = commentId;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public Byte getTagId() {
        return tagId;
    }

    public void setTagId(Byte tagId) {
        this.tagId = tagId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getFromUid() {
        return fromUid;
    }

    public void setFromUid(String fromUid) {
        this.fromUid = fromUid;
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
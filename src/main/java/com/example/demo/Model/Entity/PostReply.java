package com.example.demo.Model.Entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "post_replies")
public class PostReply {
    @Id
    @Column(name = "reply_id", nullable = false, length = 36)
    private String id;

    @Column(name = "comment_id", length = 36)
    private String commentId;

    @Column(name = "parent_reply_id", length = 36)
    private String parentReplyId;

    @Column(name = "reply_type")
    private Boolean replyType = false;

    @Column(name = "content", nullable = false, length = 4095)
    private String content;

    @Column(name = "from_uid", nullable = false, length = 36)
    private String fromUid;

    @Column(name = "to_uid", nullable = false, length = 36)
    private String toUid;

    @Column(name = "ipv_four", columnDefinition = "INT UNSIGNED")
    private Long ipvFour;

    @Column(name = "ipv_six", length = 16)
    private String ipvSix;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "modified_at", nullable = false)
    private Instant modifiedAt;

    public String getReplyId() {
        return id;
    }

    public void setReplyId(String replyId) {
        this.id = replyId;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getParentReplyId() {
        return parentReplyId;
    }

    public void setParentReplyId(String parentReplyId) {
        this.parentReplyId = parentReplyId;
    }

    public Boolean getReplyType() {
        return replyType;
    }

    public void setReplyType(Boolean replyType) {
        this.replyType = replyType;
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

    public String getToUid() {
        return toUid;
    }

    public void setToUid(String toUid) {
        this.toUid = toUid;
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
package com.example.demo.Model.Entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "comment_reply_mentions")
public class CommentReplyMention {
    @Id
    @Column(name = "post_id", nullable = false, length = 36)
    private String id;

    @Column(name = "mentioned_uid", nullable = false, length = 36)
    private String mentionedUid;

    @Column(name = "comment_id", length = 36)
    private String commentId;

    @Column(name = "reply_id", length = 36)
    private String replyId;

    @Column(name = "read_status", nullable = false)
    private Boolean readStatus = false;

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

    public String getMentionedUid() {
        return mentionedUid;
    }

    public void setMentionedUid(String mentionedUid) {
        this.mentionedUid = mentionedUid;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getReplyId() {
        return replyId;
    }

    public void setReplyId(String replyId) {
        this.replyId = replyId;
    }

    public Boolean getReadStatus() {
        return readStatus;
    }

    public void setReadStatus(Boolean readStatus) {
        this.readStatus = readStatus;
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
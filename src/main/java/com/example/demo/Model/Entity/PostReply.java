package com.example.demo.Model.Entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "post_replies")
public class PostReply {
    @Id
    @Size(max = 32)
    @Column(name = "reply_id", nullable = false, length = 32)
    private String id;

    @Size(max = 32)
    @NotNull
    @Column(name = "comment_id", nullable = false, length = 32)
    private String commentId;

    @Size(max = 32)
    @Column(name = "to_reply_id", length = 32)
    private String toReplyId;


    @Size(max = 4095)
    @NotNull
    @Column(name = "content", nullable = false, length = 4095)
    private String content;

    @Size(max = 32)
    @NotNull
    @Column(name = "from_uid", nullable = false, length = 32)
    private String fromUid;

    @Size(max = 32)
    @NotNull
    @Column(name = "to_uid", nullable = false, length = 32)
    private String toUid;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @NotNull
    @Column(name = "modified_at", nullable = false)
    private Instant modifiedAt;

}
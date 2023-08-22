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
    @Column(name = "reply_id", nullable = false)
    private Long id;

    @NotNull
    @Column(name = "comment_id", nullable = false)
    private Long commentId;

    @Column(name = "to_reply_id")
    private Long toReplyId;

    @Size(max = 4095)
    @NotNull
    @Column(name = "content", nullable = false, length = 4095)
    private String content;

    @NotNull
    @Column(name = "from_uid", nullable = false)
    private Long fromUid;

    @NotNull
    @Column(name = "to_uid", nullable = false)
    private Long toUid;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @NotNull
    @Column(name = "modified_at", nullable = false)
    private Instant modifiedAt;

}
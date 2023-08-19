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
@Table(name = "messages")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id", nullable = false)
    private Long id;

    @Size(max = 32)
    @NotNull
    @Column(name = "cr_id", nullable = false, length = 32)
    private String crId;

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
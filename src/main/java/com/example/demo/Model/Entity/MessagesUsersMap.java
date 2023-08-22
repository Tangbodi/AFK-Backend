package com.example.demo.Model.Entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@Entity
@Table(name = "messages_users_map")
public class MessagesUsersMap {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @Column(name = "message_id", nullable = false)
    private Long messageId;

    @NotNull
    @Column(name = "mentioned_uid", nullable = false)
    private Long mentionedUid;

    @NotNull
    @Column(name = "read_status", nullable = false)
    private Boolean readStatus = false;

}
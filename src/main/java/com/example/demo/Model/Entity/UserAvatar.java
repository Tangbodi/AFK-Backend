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
@Table(name = "user_avatars")
public class UserAvatar {
    @Id
    @Column(name = "user_id", nullable = false)
    private Long id;

    @Size(max = 31)
    @NotNull
    @Column(name = "avatar_type", nullable = false, length = 31)
    private String avatarType;

    @Size(max = 127)
    @NotNull
    @Column(name = "avatar_path", nullable = false, length = 127)
    private String avatarPath;

    @Size(max = 63)
    @NotNull
    @Column(name = "avatar_url", nullable = false, length = 63)
    private String avatarUrl;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @NotNull
    @Column(name = "modified_at", nullable = false)
    private Instant modifiedAt;

}
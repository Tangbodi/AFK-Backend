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
@Table(name = "users_verification_token")
public class UsersVerificationToken {
    @Id
    @Column(name = "user_id", nullable = false)
    private Long id;

    @Size(max = 32)
    @Column(name = "token", length = 32)
    private String token;

    @Size(max = 31)
    @NotNull
    @Column(name = "username", nullable = false, length = 31)
    private String username;

    @Size(max = 63)
    @NotNull
    @Column(name = "email", nullable = false, length = 63)
    private String email;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @NotNull
    @Column(name = "modified_at", nullable = false)
    private Instant modifiedAt;

}
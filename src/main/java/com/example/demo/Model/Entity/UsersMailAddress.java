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
@Table(name = "users_mail_address")
public class UsersMailAddress {
    @Id
    @Size(max = 36)
    @Column(name = "user_id", nullable = false, length = 36)
    private String id;

    @Size(max = 63)
    @Column(name = "country", length = 63)
    private String country;

    @Size(max = 15)
    @Column(name = "state", length = 15)
    private String state;

    @Size(max = 127)
    @Column(name = "address", length = 127)
    private String address;

    @Size(max = 31)
    @Column(name = "city", length = 31)
    private String city;

    @Size(max = 7)
    @Column(name = "zip", length = 7)
    private String zip;

    @Size(max = 11)
    @Column(name = "phone", length = 11)
    private String phone;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @NotNull
    @Column(name = "modified_at", nullable = false)
    private Instant modifiedAt;

}
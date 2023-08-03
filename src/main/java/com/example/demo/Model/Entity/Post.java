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
@Table(name = "posts")
public class Post {
    @Id
    @Size(max = 36)
    @Column(name = "post_id", nullable = false, length = 36)
    private String id;

    @Size(max = 255)
    @NotNull
    @Column(name = "title", nullable = false)
    private String title;

    @Size(max = 4095)
    @NotNull
    @Column(name = "text_render", nullable = false, length = 4095)
    private String textRender;

    @Column(name = "ipv_four", columnDefinition = "INT UNSIGNED")
    private Long ipvFour;

    @Size(max = 16)
    @Column(name = "ipv_six", length = 16)
    private String ipvSix;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @NotNull
    @Column(name = "modified_at", nullable = false)
    private Instant modifiedAt;

}
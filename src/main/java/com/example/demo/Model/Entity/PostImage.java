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
@Table(name = "post_images")
public class PostImage {
    @Id
    @Size(max = 17)
    @Column(name = "image_id", nullable = false, length = 17)
    private String id;

    @Size(max = 32)
    @NotNull
    @Column(name = "post_id", nullable = false, length = 32)
    private String postId;

    @Size(max = 31)
    @Column(name = "image_type", length = 31)
    private String imageType;

    @Size(max = 127)
    @NotNull
    @Column(name = "image_path", nullable = false, length = 127)
    private String imagePath;

    @Size(max = 63)
    @NotNull
    @Column(name = "image_url", nullable = false, length = 63)
    private String imageUrl;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @NotNull
    @Column(name = "modified_at", nullable = false)
    private Instant modifiedAt;

}
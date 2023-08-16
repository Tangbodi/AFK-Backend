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
    @Size(max = 36)
    @Column(name = "image_id", nullable = false, length = 36)
    private String id;

    @Size(max = 36)
    @Column(name = "post_id", length = 36)
    private String postId;

    @Size(max = 31)
    @Column(name = "image_type", length = 31)
    private String imageType;

    @Size(max = 127)
    @Column(name = "image_path", length = 127)
    private String imagePath;

    @Size(max = 63)
    @Column(name = "image_url", length = 63)
    private String imageUrl;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @NotNull
    @Column(name = "modified_at", nullable = false)
    private Instant modifiedAt;

    @Column(name = "imagedata")
    private byte[] imagedata;

}
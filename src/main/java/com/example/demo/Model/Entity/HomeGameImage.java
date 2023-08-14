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
@Table(name = "home_game_images")
public class HomeGameImage {
    @Id
    @Column(name = "image_id", nullable = false)
    private Short id;

    @Size(max = 69)
    @NotNull
    @Column(name = "image_path", nullable = false, length = 69)
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
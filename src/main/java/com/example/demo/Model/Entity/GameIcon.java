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
@Table(name = "game_icons")
public class GameIcon {
    @Id
    @Column(name = "icon_id", nullable = false)
    private Short id;

    @NotNull
    @Column(name = "genre_id", nullable = false)
    private Byte genreId;

    @Size(max = 127)
    @NotNull
    @Column(name = "game_name", nullable = false, length = 127)
    private String gameName;

    @Size(max = 69)
    @NotNull
    @Column(name = "icon_path", nullable = false, length = 69)
    private String iconPath;

    @Size(max = 127)
    @NotNull
    @Column(name = "icon_url", nullable = false, length = 127)
    private String iconUrl;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @NotNull
    @Column(name = "modified_at", nullable = false)
    private Instant modifiedAt;

}
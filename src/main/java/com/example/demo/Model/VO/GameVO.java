package com.example.demo.Model.VO;

import lombok.Data;

import java.io.Serializable;

@Data
public class GameVO implements Serializable {
    private Short gameId;
    private Byte genreId;
    private String gameName;
    private String iconUrl;
}

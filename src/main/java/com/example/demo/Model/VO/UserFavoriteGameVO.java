package com.example.demo.Model.VO;

import lombok.Data;

@Data
public class UserFavoriteGameVO {
    private Short gameId;
    private Byte genreId;
    private String gameName;
    private String iconUrl;

}

package com.example.demo.Model.VO;

import lombok.Data;

@Data
public class UserFavoritePostVO {
    private String postId;
    private String userId;
    private boolean likeStatus;
    private boolean saveStatus;
}

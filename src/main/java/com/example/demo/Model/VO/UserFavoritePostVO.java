package com.example.demo.Model.VO;

import lombok.Data;

@Data
public class UserFavoritePostVO {
    private Long postId;
    private Long userId;
    private boolean likeStatus;
    private boolean saveStatus;
}

package com.example.demo.Model.VO;

import lombok.Data;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
public class ShowPostBodyVO {
    private String postId;
    private String gameName;
    private String userId;
    private String userName;
    private String avatarURL;
    private String title;
    private String textRender;
    private String view;
    private String commentReply;
    private String like;
    private String save;
    private String likeStatus;
    private String saveStatus;
    private List<String> imageURL;
    private String createdAt;
}

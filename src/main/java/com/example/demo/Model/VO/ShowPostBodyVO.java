package com.example.demo.Model.VO;

import lombok.Data;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
public class ShowPostBodyVO {
    private String postId;
    private String userId;
    private String userName;
    private String title;
    private String textRender;
    private Integer view;
    private Integer commentReply;
    private Integer like;
    private Integer save;
    private Byte likeStatus;
    private Byte saveStatus;
    private List<String> imageURL;
    private Instant createdAt;
}

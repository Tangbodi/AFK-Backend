package com.example.demo.Model.VO;

import lombok.Data;

import java.io.Serializable;
@Data
public class RecommendationVO implements Serializable {
    private Integer AfkAnnouncement;
    private Integer FeaturedContent;
    private Integer TrendingPost;
    private Integer CommunityRecommendation;
}

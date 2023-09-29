package com.example.demo.Service.UserSettings;

import com.example.demo.Mapper.Repository.*;
import com.example.demo.Model.Entity.CommentOnPostMention;
import com.example.demo.Model.Entity.ReplyOnCommentMention;
import com.example.demo.Model.VO.ActivityVO;
import com.example.demo.Model.VO.RecommendationVO;
import com.example.demo.Model.VO.UserSettingVO;
import com.example.demo.Service.Redis.RedisService;
import com.example.demo.Service.Redis.RedisUserSettingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.jms.JMSException;
import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserSettingService {
    private static final Logger logger = LoggerFactory.getLogger(UserSettingService.class);
    private static final String ACTIVITY = "activity";
    private static final String RECOMMENDATION = "recommendation";
    private static final String USER_SETTING = "USER_SETTING";
    @Autowired
    private LikeOnPostMentionRepository likeOnPostMentionRepository;
    @Autowired
    private LikeOnCommentMentionRepository likeOnCommentMentionRepository;
    @Autowired
    private SaveOnPostMentionRepository saveOnPostMentionRepository;
    @Autowired
    private ReplyOnCommentMentionRepository replyOnCommentMentionRepository;
    @Autowired
    private CommentOnPostMentionRepository commentOnPostMentionRepository;
    @Autowired
    private PostOnSavedGameMentionRepository postOnSavedGameMentionRepository;
    @Autowired
    private MentionOfUsernameRepository mentionOfUsernameRepository;
    @Autowired
    private AfkAnnouncementRepository afkAnnouncementRepository;
    @Autowired
    private FeaturedContentRepository featuredContentRepository;
    @Autowired
    private TrendingPostRepository trendingPostRepository;
    @Autowired
    private CommunityRecommendationRepository communityRecommendationRepository;
    @Autowired
    private RedisService redisService;
    @Lazy
    @Autowired
    private RedisUserSettingService redisUserSettingService;
    @Transactional
    public void SaveUserSetting(Map<String,Object> userSettingVOMap, Long userId) {
        logger.info("Saving User Setting");
        try {
            //user activity setting
            Object VOMap = userSettingVOMap.get(ACTIVITY);
            if (VOMap instanceof LinkedHashMap) {
                LinkedHashMap<String,Integer> activityVOMap = (LinkedHashMap<String, Integer>) VOMap;
                SaveActivitySetting(activityVOMap, userId);
                userSettingVOMap.put(ACTIVITY, activityVOMap);
            } else {
                //
            }
            //user recommendation setting
            Object VOMap2 = userSettingVOMap.get(RECOMMENDATION);
            if(VOMap2 instanceof LinkedHashMap) {
                LinkedHashMap<String,Integer> recommendationVOMap = (LinkedHashMap<String, Integer>) VOMap2;
                SaveRecommendationSetting(recommendationVOMap, userId);
                userSettingVOMap.put(RECOMMENDATION, recommendationVOMap);
            } else {
                //
            }
        } catch (Exception e) {
            logger.error("Failed to change user setting: {}", e.getMessage(), e);
        }
    }
    @Transactional
    private void SaveActivitySetting(LinkedHashMap<String,Integer> activityVOMap, Long userId) {
        logger.info("Saving Activity Setting");
        likeOnPostMentionRepository.UpdateStatus(activityVOMap.get("likeOnPost"), userId);
        likeOnCommentMentionRepository.UpdateStatus(activityVOMap.get("likeOnComment"), userId);
        saveOnPostMentionRepository.UpdateStatus(activityVOMap.get("saveOnPost"), userId);
        commentOnPostMentionRepository.UpdateStatus(activityVOMap.get("commentOnPost"), userId);
        replyOnCommentMentionRepository.UpdateStatus(activityVOMap.get("replyOnComment"), userId);
        postOnSavedGameMentionRepository.UpdateStatus(activityVOMap.get("postOnSavedGame"), userId);
        mentionOfUsernameRepository.UpdateStatus(activityVOMap.get("mentionOfUsername"), userId);
    }
    @Transactional
    private void SaveRecommendationSetting(LinkedHashMap<String,Integer> recommendationVOMap, Long userId){
        logger.info("Saving Recommendation Setting");
        afkAnnouncementRepository.UpdateStatus(recommendationVOMap.get("afkAnnouncement"), userId);
        featuredContentRepository.UpdateStatus(recommendationVOMap.get("featuredContent"), userId);
        trendingPostRepository.UpdateStatus(recommendationVOMap.get("trendingPost"), userId);
        communityRecommendationRepository.UpdateStatus(recommendationVOMap.get("communityRecommendation"), userId);
    }

    public Map<String, Object> GetUserSetting(Long userId) {
        logger.info("Getting User Setting: {}", userId);
        try {
            String key = USER_SETTING + ":::" + userId;
            if (redisService.MemberExists(USER_SETTING, userId)) {
                logger.info("USER_SETTING exists in Redis cache: {}");
            } else {
                logger.info("USER_SETTING doesn't exist in Redis cache: {}");
                redisService.AddSet(USER_SETTING, userId);
            }
            List<Map<Short, Object>> userSetting = commentOnPostMentionRepository.findSettingByUserId(userId);
            if (!userSetting.isEmpty()) {
                logger.info("User Setting found: {}", userId);
                Map<String, Object> userSettingVOMap = TransferToUserSettingVO(userSetting);
                redisService.AddHashSet(key, userId.toString(), userSettingVOMap);
                return userSettingVOMap;
            } else {
                logger.info("No User Setting found: {}", userId);
                return null;
            }
        } catch (Exception e) {
            logger.error("Failed to get User Setting: {}", e.getMessage(), e);
            return null;
        }
    }

    private Map<String, Object> TransferToUserSettingVO(List<Map<Short, Object>> usersSetting) {
        logger.info("Transferring User Setting to VO");
        try {
//            UserSettingVO userSettingVO = new UserSettingVO();
            ActivityVO activityVO = new ActivityVO();
            RecommendationVO recommendationVO = new RecommendationVO();
            Map<String, Object> userSettingVOMap = new HashMap<>();
            for (Map<Short, Object> map : usersSetting) {
                activityVO.setCommentOnPost(map.get("comment_on_post") == Boolean.TRUE ? 1 : 0);
                activityVO.setLikeOnComment(map.get("like_on_comment") == Boolean.TRUE ? 1 : 0);
                activityVO.setLikeOnPost(map.get("like_on_post") == Boolean.TRUE ? 1 : 0);
                activityVO.setPostOnSavedGame(map.get("post_on_saved_game") == Boolean.TRUE ? 1 : 0);
                activityVO.setReplyOnComment(map.get("reply_on_comment") == Boolean.TRUE ? 1 : 0);
                activityVO.setSaveOnPost(map.get("save_on_post") == Boolean.TRUE ? 1 : 0);
                activityVO.setMentionOfUsername(map.get("mention_of_username") == Boolean.TRUE ? 1 : 0);
                userSettingVOMap.put(ACTIVITY, activityVO);
                recommendationVO.setAfkAnnouncement(map.get("afk_announcement") == Boolean.TRUE ? 1 : 0);
                recommendationVO.setCommunityRecommendation(map.get("community_recommendation") == Boolean.TRUE ? 1 : 0);
                recommendationVO.setFeaturedContent(map.get("featured_content") == Boolean.TRUE ? 1 : 0);
                recommendationVO.setTrendingPost(map.get("trending_post") == Boolean.TRUE ? 1 : 0);
                userSettingVOMap.put(RECOMMENDATION, recommendationVO);
            }
            return userSettingVOMap;
        } catch (Exception e) {
            logger.error("Failed to transfer User Setting to VO: {}", e.getMessage(), e);
            return null;
        }
    }

    public boolean CheckCommentOnPostMention(Long userId) {
        logger.info("Checking comment on post mention setting for user:{}", userId);
        try {
            CommentOnPostMention commentOnPostMention = commentOnPostMentionRepository.findById(userId).orElse(null);
            if (commentOnPostMention == null) {
                logger.info("User not found");
                return false;
            } else {
                logger.info("User found");
                if (commentOnPostMention.getMentionOn() == false) {
                    logger.info("Comment on post mention setting is off");
                    return false;
                } else {
                    logger.info("Comment on post mention setting is on");
                    return true;
                }
            }
        } catch (Exception e) {
            logger.error("Failed to check comment on post mention setting: {}", e.getMessage(), e);
            return false;
        }
    }

    public boolean CheckReplyOnCommentMention(Long userId) throws JMSException {
        logger.info("Checking reply on comment mention setting for user:{}", userId);
        try {
            ReplyOnCommentMention replyOnCommentMention = replyOnCommentMentionRepository.findById(userId).orElse(null);
            if (replyOnCommentMention == null) {
                logger.info("User not found");
            } else {
                logger.info("User found");
                if (replyOnCommentMention.getMentionOn() == false) {
                    logger.info("Reply on comment mention setting is off");
                    return false;
                } else {
                    logger.info("Reply on comment mention setting is on");
                    return true;
                }
            }
        } catch (Exception e) {
            logger.error("Failed to check reply on comment mention setting: {}", e.getMessage(), e);

        }
        return false;
    }

    public boolean CheckLikeOnPostMention(Long userId) {
        logger.info("Checking like on post mention setting for user ID: {}", userId);
        try {
            boolean status = likeOnPostMentionRepository.findById(userId).orElse(null).getMentionOn();
            if (status == false) {
                logger.info("Like on post mention setting is off");
                return false;
            } else {
                logger.info("Like on post mention setting is on");
                return true;
            }
        } catch (Exception e) {
            logger.error("Error checking like on post mention setting: {}", e.getMessage(), e);
            return false;
        }
    }

    public boolean CheckLikeOnCommentMention(Long userId) {
        logger.info("Checking like on comment mention setting for user ID: {}", userId);
        try {
            boolean status = likeOnCommentMentionRepository.findById(userId).orElse(null).getMentionOn();
            if (status == false) {
                logger.info("Like on comment mention is off");
                return false;
            } else {
                logger.info("Like on comment mention is on");
                return true;
            }
        } catch (Exception e) {
            logger.error("Error checking like on comment mention setting: {}", e.getMessage(), e);
            return false;
        }
    }

    public boolean CheckSaveOnPostMention(Long userId) {
        logger.info("Checking save on post mention setting for user ID: {}", userId);
        try {
            boolean status = saveOnPostMentionRepository.findById(userId).orElse(null).getMentionOn();
            if (status == false) {
                logger.info("Save on post mention setting is off");
                return false;
            } else {
                logger.info("Save on post mention setting is on");
                return true;
            }
        } catch (Exception e) {
            logger.error("Error checking save on post mention setting: {}", e.getMessage(), e);
            return false;
        }
    }
    public boolean CheckMentionOfUsernameMention(Long userId){
        logger.info("Checking mention of username mention setting for user ID: {}", userId);
        try {
            boolean status = mentionOfUsernameRepository.findById(userId).orElse(null).getMentionOn();
            if (status == false) {
                logger.info("Mention of username mention setting is off");
                return false;
            } else {
                logger.info("Mention of username mention setting is on");
                return true;
            }
        } catch (Exception e) {
            logger.error("Error checking mention of username mention setting: {}", e.getMessage(), e);
            return false;
        }
    }
    public boolean CheckPostOnSavedGameMention(Long userId) {
        logger.info("Checking post on saved game mention setting for user ID: {}", userId);
        try {
            boolean status = postOnSavedGameMentionRepository.findById(userId).orElse(null).getMentionOn();
            if (status == false) {
                logger.info("Post on saved game mention setting is off");
                return false;
            } else {
                logger.info("Post on saved game mention setting is on");
                return true;
            }
        } catch (Exception e) {
            logger.error("Error checking post on saved game mention setting: {}", e.getMessage(), e);
            return false;
        }
    }
}

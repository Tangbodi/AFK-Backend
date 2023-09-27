package com.example.demo.Service.UsersInfo;

import com.example.demo.Mapper.Repository.*;
import com.example.demo.Model.Entity.CommentOnPostMention;
import com.example.demo.Model.Entity.ReplyOnCommentMention;
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
import java.util.List;
import java.util.Map;

@Service
public class UserSettingService {
    private static final Logger logger = LoggerFactory.getLogger(UserSettingService.class);
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
    private RedisService redisService;
    @Lazy
    @Autowired
    private RedisUserSettingService redisUserSettingService;

    @Async("MultiExecutor")
    @Transactional
    public void SaveUserSetting(UserSettingVO userSettingVO, Long userId) {
        logger.info("Saving User Setting");
        try {
            likeOnPostMentionRepository.UpdateStatus(userSettingVO.getLikeOnPost(), userId);
            likeOnCommentMentionRepository.UpdateStatus(userSettingVO.getLikeOnComment(), userId);
            saveOnPostMentionRepository.UpdateStatus(userSettingVO.getSaveOnPost(), userId);
            commentOnPostMentionRepository.UpdateStatus(userSettingVO.getCommentOnPost(), userId);
            replyOnCommentMentionRepository.UpdateStatus(userSettingVO.getReplyOnComment(), userId);
            postOnSavedGameMentionRepository.UpdateStatus(userSettingVO.getPostOnSavedGame(), userId);
            mentionOfUsernameRepository.UpdateStatus(userSettingVO.getMentionOfUsername(), userId);
        } catch (Exception e) {
            logger.error("Failed to change user setting: {}", e.getMessage(), e);
        }
    }

    public UserSettingVO GetUserSetting(Long userId) {
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
                UserSettingVO userSettingVO = TransferToUserSettingVO(userSetting);
                redisService.AddHashSet(key, userId.toString(), userSettingVO);
                return userSettingVO;
            } else {
                logger.info("No User Setting found: {}", userId);
                return null;
            }
        } catch (Exception e) {
            logger.error("Failed to get User Setting: {}", e.getMessage(), e);
            return null;
        }
    }

    private UserSettingVO TransferToUserSettingVO(List<Map<Short, Object>> usersSetting) {
        logger.info("Transferring User Setting to VO");
        try {
            UserSettingVO userSettingVO = new UserSettingVO();
            for (Map<Short, Object> map : usersSetting) {
                userSettingVO.setCommentOnPost(map.get("comment_on_post") == Boolean.TRUE ? 1 : 0);
                userSettingVO.setLikeOnComment(map.get("like_on_comment") == Boolean.TRUE ? 1 : 0);
                userSettingVO.setLikeOnPost(map.get("like_on_post") == Boolean.TRUE ? 1 : 0);
                userSettingVO.setPostOnSavedGame(map.get("post_on_saved_game") == Boolean.TRUE ? 1 : 0);
                userSettingVO.setReplyOnComment(map.get("reply_on_comment") == Boolean.TRUE ? 1 : 0);
                userSettingVO.setSaveOnPost(map.get("save_on_post") == Boolean.TRUE ? 1 : 0);
                userSettingVO.setMentionOfUsername(map.get("mention_of_username") == Boolean.TRUE ? 1 : 0);
            }
            return userSettingVO;
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

package com.example.demo.Service.UsersVerification;

import com.example.demo.Mapper.Repository.UserVerificationRepository;
import com.example.demo.Model.DTO.UserRegisterDTO;
import com.example.demo.Model.Entity.UsersVerificationToken;
import com.example.demo.Service.EmailValidation.ProcessEmailService;
import com.example.demo.Service.UserSettings.*;
import com.example.demo.Service.UsersAuth.UserAuthService;
import com.example.demo.Service.UsersInfo.UserInfoService;
import com.example.demo.Util.UUIDCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.time.Instant;

@Service
public class UserVerificationService {
    private static final Logger logger = LoggerFactory.getLogger(UserVerificationService.class);
    @Lazy
    @Autowired
    private UserInfoService userInfoService;
    @Autowired
    private UserVerificationRepository userVerificationRepository;
    @Lazy
    @Autowired
    private ProcessEmailService processEmailService;
    @Lazy
    @Autowired
    private UserAuthService userAuthService;
    @Autowired
    private CommentOnPostMentionService commentOnPostMentionService;
    @Autowired
    private ReplyOnCommentMentionService replyOnCommentMentionService;
    @Autowired
    private LikeOnCommentMentionService likeOnCommentMentionService;
    @Autowired
    private LikeOnPostMentionService likeOnPostMentionService;
    @Autowired
    private SaveOnPostMentionService saveOnPostMentionService;
    @Autowired
    private PostOnSavedGameService postOnSavedGameService;
    @Autowired
    private MentionOfUsernameService mentionOfUsernameService;
    @Autowired
    private AfkAnnouncementService afkAnnouncementService;
    @Autowired
    private CommunityRecommendationService communityRecommendationService;
    @Autowired
    private FeaturedContentService featuredContentService;
    @Autowired
    private TrendingPostService trendingPostService;

    @Transactional
    public boolean SetUserRegistrationVerificationToken(String token, UserRegisterDTO userRegisterDTO) {
        logger.info("Setting UserRegistrationVerificationToken");
        try {
            UsersVerificationToken usersVerificationToken = new UsersVerificationToken();
            usersVerificationToken.setId(userRegisterDTO.getUserId());
            usersVerificationToken.setToken(token);
            usersVerificationToken.setUsername(userRegisterDTO.getUsername());
            usersVerificationToken.setEmail(userRegisterDTO.getEmail());
            usersVerificationToken.setCreatedAt(userRegisterDTO.getCreatedAt());
            usersVerificationToken.setModifiedAt(userRegisterDTO.getCreatedAt());
            userVerificationRepository.save(usersVerificationToken);

            logger.info("Saved UserRegistrationVerificationToken successfully");
            return true;
        } catch (Exception e) {
            logger.error("Failed to set token: {}", e.getMessage(), e);
            return false;
        }
    }

    @Transactional
    public void SetUserLoginVerificationToken(String username, HttpServletRequest request) {
        logger.info("Setting UserVerificationToken");
        try {
            logger.info("Finding UsersVerificationToken via username: {}", username);
            UsersVerificationToken usersVerificationToken = userVerificationRepository.findByUsername(username);
            if (usersVerificationToken != null) {
                logger.info("Found user: {}", username);
                String token = UUIDCreator.CreateUUID();
                usersVerificationToken.setToken(token);
                usersVerificationToken.setModifiedAt(Instant.now());
                userVerificationRepository.save(usersVerificationToken);
                logger.info("Saved UserVerificationToken successfully");
                String siteURL = request.getRequestURL().toString();
                siteURL.replace(request.getServletPath(), "");
                processEmailService.ProcessLoginEmailValidation(siteURL, usersVerificationToken.getEmail(), token, username);
            } else {
                logger.info("User not found: {}", username);
            }
        } catch (Exception e) {
            logger.error("Failed to set token: {}", e.getMessage(), e);
        }
    }

    public void FindUserVerificationByToken(String token) {
        logger.info("Getting UsersVerificationToken: {}", token);
        try {
           UsersVerificationToken usersVerificationToken = userVerificationRepository.findByToken(token);
            if (usersVerificationToken != null) {
                logger.info("Found UsersVerificationToken: token={}, userId={}", usersVerificationToken.getToken(), usersVerificationToken.getId());
                userAuthService.UpdateUserAuth(usersVerificationToken.getId());
                RemoveToken(usersVerificationToken);
                //user activity setting
                commentOnPostMentionService.SaveCommentOnPostMention(usersVerificationToken);
                replyOnCommentMentionService.SaveReplyOnCommentMention(usersVerificationToken);
                likeOnPostMentionService.SetLikeOnPostMention(usersVerificationToken);
                likeOnCommentMentionService.SetLikeOnCommentMention(usersVerificationToken);
                saveOnPostMentionService.SetSaveOnPostMention(usersVerificationToken);
                mentionOfUsernameService.SetMentionOfUsername(usersVerificationToken);
                postOnSavedGameService.SetPostOnSavedGame(usersVerificationToken);
                //usr recommendation setting
                afkAnnouncementService.SaveAfkAnnouncement(usersVerificationToken);
                communityRecommendationService.SaveCommunityRecommendation(usersVerificationToken);
                featuredContentService.SaveFeaturedContent(usersVerificationToken);
                trendingPostService.SaveTrendingPost(usersVerificationToken);
            } else {
                //
            }
        } catch (Exception e) {
            logger.error("Failed to get UsersVerificationToken: {}", e.getMessage(), e);
        }
    }

    @Transactional
    public void RemoveToken(UsersVerificationToken usersVerificationToken) {
        logger.info("Removing token via UsersVerificationToken: {}", usersVerificationToken.getToken());
        try {
            usersVerificationToken.setToken(null);
            usersVerificationToken.setModifiedAt(Instant.now());
            userVerificationRepository.save(usersVerificationToken);
            logger.info("Removed token successfully");

        } catch (Exception e) {
            logger.error("Failed to remove token: {}", e.getMessage(), e);

        }
    }

    @Transactional
    public void UpdateUserEmail(String userId, String newEmail) {
        logger.info("Updating Email for user: userId={}, email={}", userId, newEmail);
        try {
            Long userIdLong = Long.parseLong(userId);
            UsersVerificationToken usersVerificationToken = userVerificationRepository.findById(userIdLong).orElse(null);
            if (usersVerificationToken != null) {
                logger.info("Old email: {}", usersVerificationToken.getEmail());
                logger.info("Updating email to: {}", newEmail);
                usersVerificationToken.setEmail(newEmail);
                usersVerificationToken.setToken(null);
                usersVerificationToken.setModifiedAt(Instant.now());
                userVerificationRepository.save(usersVerificationToken);
                logger.info("Updated email successfully");
            } else {
                logger.info("User not found: {}", userId);
            }
        } catch (Exception e) {
            logger.error("Failed to update email: {}", e.getMessage(), e);
        }
    }
}

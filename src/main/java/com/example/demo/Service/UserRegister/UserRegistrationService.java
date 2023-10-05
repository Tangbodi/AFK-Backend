package com.example.demo.Service.UserRegister;

import com.example.demo.Mapper.Repository.UserInfoRepository;
import com.example.demo.Mapper.Repository.UsersLoginRepository;
import com.example.demo.Model.DTO.UserRegisterDTO;
import com.example.demo.Model.Entity.UsersInfo;
import com.example.demo.Model.Entity.UsersLogin;
import com.example.demo.Service.UserSettings.*;
import com.example.demo.Service.UsersAuth.UserAuthService;
import com.example.demo.Service.UsersInfo.UserMailAddressService;
import com.example.demo.Service.UsersInfo.UserInfoService;
import com.example.demo.Util.Snowflake;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Instant;

@Service
public class UserRegistrationService {
    private static final Logger logger = LoggerFactory.getLogger(UserRegistrationService.class);
    @Autowired
    private UsersLoginRepository usersLoginRepository;
    @Autowired
    private UserInfoRepository userInfoRepository;
    @Autowired
    private UserInfoService userInfoService;
    @Autowired
    private UserAuthService userAuthService;
    @Autowired
    private UserMailAddressService userMailAddressService;
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

    public UsersInfo CheckUsernameExists(String username) {
        String usernameCapitalized = CapitalizeFirstLetter(username);
        UsersInfo usersInfo = userInfoService.CheckUsernameExists(usernameCapitalized);
        return usersInfo;
    }

    public UsersInfo CheckEmailExists(String email) {
        UsersInfo usersInfo = userInfoService.CheckEmailExists(email);
        return usersInfo;
    }

    @Transactional
    public UsersLogin RegisterUser(UserRegisterDTO userRegisterDTO){
        logger.info("Registering user: {}", userRegisterDTO.getUsername());
        try {
            logger.info("Creating UUID for user: {}", userRegisterDTO.getUsername());
            Long snowflakeId = Snowflake.generateUniqueId();
            userRegisterDTO.setUserId(snowflakeId);
            userRegisterDTO.setCreatedAt(Instant.now());
            String username = CapitalizeFirstLetter(userRegisterDTO.getUsername());
            userRegisterDTO.setUsername(username);
            logger.info("Saving User :{}", username);
            UsersLogin user = new UsersLogin();
            user.setId(snowflakeId);
            user.setUsername(username);
            String encodedPassword = BCrypt.hashpw(userRegisterDTO.getPassword(), BCrypt.gensalt());
            user.setPassword(encodedPassword);
            user.setCreatedAt(userRegisterDTO.getCreatedAt());
            user.setModifiedAt(userRegisterDTO.getCreatedAt());
            //postOnSavedGame
            UsersLogin savedUser = usersLoginRepository.save(user);
            if(savedUser != null){
                //user info setting
                userAuthService.SaveUsersAuth(userRegisterDTO);
                userInfoService.SaveUserInfo(userRegisterDTO);
                //user activity setting
                commentOnPostMentionService.SaveCommentOnPostMention(userRegisterDTO);
                replyOnCommentMentionService.SaveReplyOnCommentMention(userRegisterDTO);
                likeOnPostMentionService.SetLikeOnPostMention(userRegisterDTO);
                likeOnCommentMentionService.SetLikeOnCommentMention(userRegisterDTO);
                saveOnPostMentionService.SetSaveOnPostMention(userRegisterDTO);
                mentionOfUsernameService.SetMentionOfUsername(userRegisterDTO);
                postOnSavedGameService.SetPostOnSavedGame(userRegisterDTO);
                //usr recommendation setting
                afkAnnouncementService.SaveAfkAnnouncement(userRegisterDTO);
                communityRecommendationService.SaveCommunityRecommendation(userRegisterDTO);
                featuredContentService.SaveFeaturedContent(userRegisterDTO);
                trendingPostService.SaveTrendingPost(userRegisterDTO);
            } else {
                //
            }
            return savedUser;
        } catch (Exception e) {
            logger.error("Failed to register user: {}", e.getMessage(),e);
            throw new RuntimeException("Failed to register user "+e);
        }
    }
    private String CapitalizeFirstLetter(String username){
        return username.substring(0, 1).toUpperCase() + username.substring(1).toLowerCase();
    }
}

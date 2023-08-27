package com.example.demo.Service.UserFavoritePost;

import com.example.demo.Repository.UserFavoritePostRepository;
import com.example.demo.Model.DTO.UserLikesSavesPostDTO;
import com.example.demo.Model.Entity.UsersFavoritePost;
import com.example.demo.Model.Entity.UsersFavoritePostId;
import com.example.demo.Model.VO.UserFavoritePostVO;
import com.example.demo.Service.Posts.PostInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Instant;

@Service
public class UserFavoritePostService {
    private static final Logger logger = LoggerFactory.getLogger(UserFavoritePostService.class);
    @Autowired
    private UserFavoritePostRepository userFavoritePostRepository;
    @Autowired
    private PostInfoService postInfoService;

    @Transactional
    public boolean SetUserLikePost(UsersFavoritePostId usersFavoritePostId) {
        logger.info("Setting user like post for user ID: {}, post ID: {}", usersFavoritePostId.getUserId(), usersFavoritePostId.getPostId());
        try {

            //create user favorite post if not exist
            UsersFavoritePost usersFavoritePost = userFavoritePostRepository.findById(usersFavoritePostId)
                    .orElseGet(() -> CreateUserLikePost(usersFavoritePostId));
            usersFavoritePost.setLikeStatus(!usersFavoritePost.getLikeStatus());
            usersFavoritePost.setModifiedAt(Instant.now());
            userFavoritePostRepository.save(usersFavoritePost);
            //update post liked count
            postInfoService.UpdatePostLikeCount(usersFavoritePostId.getPostId());
            logger.info("User like post saved successfully for user ID: {}, post ID: {}", usersFavoritePostId.getUserId(), usersFavoritePostId.getPostId());
            return usersFavoritePost.getLikeStatus();
        } catch (Exception e) {
            logger.error("Error setting user like post: {}", e.getMessage(), e);
        }
        return false;
    }

    public UsersFavoritePost CreateUserLikePost(UsersFavoritePostId usersFavoritePostId) {
        logger.info("Creating user like post for user ID: {}, post ID: {}", usersFavoritePostId.getUserId(), usersFavoritePostId.getPostId());

        UsersFavoritePost usersFavoritePost = new UsersFavoritePost();
        usersFavoritePost.setId(usersFavoritePostId);
        usersFavoritePost.setSaveStatus(false);
        usersFavoritePost.setLikeStatus(false);
        usersFavoritePost.setCreatedAt(Instant.now());
        usersFavoritePost.setModifiedAt(Instant.now());

        logger.info("Created user favorite post for user ID: {}, post ID: {}", usersFavoritePostId.getUserId(), usersFavoritePostId.getPostId());
        return usersFavoritePost;
    }

    @Transactional
    public Boolean SetUserSavePost(UsersFavoritePostId usersFavoritePostId) {
        logger.info("Setting user save post for user ID: {}, post ID: {}", usersFavoritePostId.getUserId(), usersFavoritePostId.getPostId());
        try {

            UsersFavoritePost usersFavoritePost = userFavoritePostRepository.findById(usersFavoritePostId)
                    .orElseGet(() -> CreateUserSavePost(usersFavoritePostId));
            usersFavoritePost.setSaveStatus(!usersFavoritePost.getSaveStatus());
            usersFavoritePost.setModifiedAt(Instant.now());
            userFavoritePostRepository.save(usersFavoritePost);
            logger.info("User like post saved successfully for user ID: {}, post ID: {}", usersFavoritePostId.getUserId(), usersFavoritePostId.getPostId());
            postInfoService.UpdatePostSaveCount(usersFavoritePostId.getPostId());
            return usersFavoritePost.getLikeStatus();
        } catch (Exception e) {
            logger.error("Error setting user like post: {}", e.getMessage(), e);
        }
        return false;
    }
    public UsersFavoritePost CreateUserSavePost(UsersFavoritePostId usersFavoritePostId) {
        logger.info("Creating user save post for user ID: {}, post ID: {}", usersFavoritePostId.getUserId(), usersFavoritePostId.getPostId());

        UsersFavoritePost usersFavoritePost = new UsersFavoritePost();
        usersFavoritePost.setId(usersFavoritePostId);
        usersFavoritePost.setLikeStatus(false);
        usersFavoritePost.setSaveStatus(false);
        usersFavoritePost.setCreatedAt(Instant.now());
        usersFavoritePost.setModifiedAt(Instant.now());

        logger.info("Created user favorite post for user ID: {}, post ID: {}", usersFavoritePostId.getUserId(), usersFavoritePostId.getPostId());
        return usersFavoritePost;
    }
    public UserFavoritePostVO GetUserFavoritePostStatus(UserLikesSavesPostDTO userLikesSavesPostDTO){
        logger.info("Getting user favorite post status for user ID: {}, post ID: {}", userLikesSavesPostDTO.getUserId(), userLikesSavesPostDTO.getPostId());
        try{
            UsersFavoritePostId usersFavoritePostId = new UsersFavoritePostId();
            usersFavoritePostId.setPostId(userLikesSavesPostDTO.getPostId());
            usersFavoritePostId.setUserId(userLikesSavesPostDTO.getUserId());
            UsersFavoritePost usersFavoritePost = userFavoritePostRepository.findById(usersFavoritePostId).orElse(null);
            if (usersFavoritePost != null) {
                logger.info("User favorite post status found for user ID: {}, post ID: {}", usersFavoritePostId.getUserId(), usersFavoritePostId.getPostId());
                return TransferToUserFavoritePostVO(usersFavoritePost);
            } else {
                logger.info("No user favorite post status found for user ID: {}, post ID: {}", usersFavoritePostId.getUserId(), usersFavoritePostId.getPostId());
                return null;
            }
        } catch (Exception e) {
            logger.error("Error getting user favorite post status: {}", e.getMessage(), e);
            return null;
        }
    }
    private static UserFavoritePostVO TransferToUserFavoritePostVO(UsersFavoritePost usersFavoritePost) {
        logger.info("Transferring user favorite post status to VO for user ID: {}, post ID: {}", usersFavoritePost.getId().getUserId(), usersFavoritePost.getId().getPostId());
        UserFavoritePostVO userFavoritePostVO = new UserFavoritePostVO();
        userFavoritePostVO.setUserId(usersFavoritePost.getId().getUserId());
        userFavoritePostVO.setPostId(usersFavoritePost.getId().getPostId());
        userFavoritePostVO.setLikeStatus(usersFavoritePost.getLikeStatus());
        userFavoritePostVO.setSaveStatus(usersFavoritePost.getSaveStatus());

        logger.info("Transferred user favorite post status to VO for user ID: {}, post ID: {}", usersFavoritePost.getId().getUserId(), usersFavoritePost.getId().getPostId());
        return userFavoritePostVO;
    }
}

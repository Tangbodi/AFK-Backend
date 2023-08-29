package com.example.demo.Service.UserLikeSave;

import com.example.demo.Model.DTO.ObjectUserDTO;
import com.example.demo.Model.Entity.*;
import com.example.demo.Model.VO.UserFavoritePostVO;
import com.example.demo.Repository.*;
import com.example.demo.Service.Comments.CommentInfoService;
import com.example.demo.Service.Posts.PostInfoService;
import com.example.demo.Service.Replies.ReplyInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigInteger;
import java.time.Instant;
import java.util.List;
import java.util.Map;

@Service
public class UserLikeSaveService {
    private static final Logger logger = LoggerFactory.getLogger(UserLikeSaveService.class);
    @Autowired
    private UserFavoritePostRepository userFavoritePostRepository;
    @Autowired
    private PostInfoService postInfoService;
    @Autowired
    private UserLikeCommentRepository userLikeCommentRepository;
    @Autowired
    private UserLikeReplyRepository userLikeReplyRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private ReplyRepository replyRepository;
    @Autowired
    private CommentInfoService commentInfoService;
    @Autowired
    private ReplyInfoService replyInfoService;

    @Transactional
    public void SetUserLikePost(List<ObjectUserDTO> objectUserDTOList) {
        logger.info("Setting user like post");
        try {
            for (ObjectUserDTO objectUserDTO : objectUserDTOList) {
                logger.info("Setting user like post for user ID: {}, post ID: {}", objectUserDTO.getUserId(), objectUserDTO.getObjectId());
                UsersFavoritePostId usersFavoritePostId = new UsersFavoritePostId();
                usersFavoritePostId.setPostId(objectUserDTO.getObjectId());
                usersFavoritePostId.setUserId(objectUserDTO.getUserId());
                //create user favorite post if not exist
                UsersFavoritePost usersFavoritePost = userFavoritePostRepository.findById(usersFavoritePostId)
                        .orElseGet(() -> CreateUserLikePost(usersFavoritePostId, objectUserDTO.getCreatedAt()));
                usersFavoritePost.setLikeStatus(!usersFavoritePost.getLikeStatus());
                usersFavoritePost.setModifiedAt(objectUserDTO.getCreatedAt());
                userFavoritePostRepository.save(usersFavoritePost);
                logger.info("User like post saved successfully for user ID: {}, post ID: {}", usersFavoritePostId.getUserId(), usersFavoritePostId.getPostId());
                //update post liked count
                postInfoService.UpdatePostLikeCount(usersFavoritePostId.getPostId());
            }

        } catch (Exception e) {
            logger.error("Error setting user like post: {}", e.getMessage(), e);
        }
    }

    public UsersFavoritePost CreateUserLikePost(UsersFavoritePostId usersFavoritePostId, Instant createdAt) {
        logger.info("Creating user like post for user ID: {}, post ID: {}", usersFavoritePostId.getUserId(), usersFavoritePostId.getPostId());

        UsersFavoritePost usersFavoritePost = new UsersFavoritePost();
        usersFavoritePost.setId(usersFavoritePostId);
        usersFavoritePost.setSaveStatus(false);
        usersFavoritePost.setLikeStatus(false);
        usersFavoritePost.setCreatedAt(createdAt);
        usersFavoritePost.setModifiedAt(createdAt);

        logger.info("Created user like post for user ID: {}, post ID: {}", usersFavoritePostId.getUserId(), usersFavoritePostId.getPostId());
        return usersFavoritePost;
    }

    @Transactional
    public void SetUserSavePost(List<ObjectUserDTO> objectUserDTOList) {
        logger.info("Setting user save post");
        try {
            for (ObjectUserDTO objectUserDTO : objectUserDTOList) {
                logger.info("Setting user save post for user ID: {}, post ID: {}", objectUserDTO.getUserId(), objectUserDTO.getObjectId());
                UsersFavoritePostId usersFavoritePostId = new UsersFavoritePostId();
                usersFavoritePostId.setPostId(objectUserDTO.getObjectId());
                usersFavoritePostId.setUserId(objectUserDTO.getUserId());

                UsersFavoritePost usersFavoritePost = userFavoritePostRepository.findById(usersFavoritePostId)
                        .orElseGet(() -> CreateUserSavePost(usersFavoritePostId));
                usersFavoritePost.setSaveStatus(!usersFavoritePost.getSaveStatus());
                usersFavoritePost.setModifiedAt(objectUserDTO.getCreatedAt());
                userFavoritePostRepository.save(usersFavoritePost);
                logger.info("User save post saved successfully for user ID: {}, post ID: {}", usersFavoritePostId.getUserId(), usersFavoritePostId.getPostId());
                //update post save count
                postInfoService.UpdatePostSaveCount(usersFavoritePostId.getPostId());
            }
        } catch (Exception e) {
            logger.error("Error setting user save post: {}", e.getMessage(), e);
        }

    }

    public UsersFavoritePost CreateUserSavePost(UsersFavoritePostId usersFavoritePostId) {
        logger.info("Creating user save post for user ID: {}, post ID: {}", usersFavoritePostId.getUserId(), usersFavoritePostId.getPostId());

        UsersFavoritePost usersFavoritePost = new UsersFavoritePost();
        usersFavoritePost.setId(usersFavoritePostId);
        usersFavoritePost.setLikeStatus(false);
        usersFavoritePost.setSaveStatus(false);
        usersFavoritePost.setCreatedAt(Instant.now());
        usersFavoritePost.setModifiedAt(Instant.now());

        logger.info("Created user save post for user ID: {}, post ID: {}", usersFavoritePostId.getUserId(), usersFavoritePostId.getPostId());
        return usersFavoritePost;
    }

    @Transactional
    public void SetUserLikeComment(List<ObjectUserDTO> objectUserDTOList) {
        logger.info("Setting user like comment");
        try {
            for (ObjectUserDTO objectUserDTO : objectUserDTOList) {
                logger.info("Setting user like comment for user ID: {}, comment ID: {}", objectUserDTO.getUserId(), objectUserDTO.getObjectId());
                UsersLikeCommentId usersLikeCommentId = new UsersLikeCommentId();
                usersLikeCommentId.setCommentId(objectUserDTO.getObjectId());
                usersLikeCommentId.setUserId(objectUserDTO.getUserId());

                UsersLikeComment usersLikeComment = userLikeCommentRepository.findById(usersLikeCommentId)
                        .orElseGet(() -> CreateUserLikeComment(usersLikeCommentId));
                usersLikeComment.setLikeStatus(!usersLikeComment.getLikeStatus());
                usersLikeComment.setModifiedAt(objectUserDTO.getCreatedAt());
                userLikeCommentRepository.save(usersLikeComment);
                logger.info("User like comment saved successfully for user ID: {}, comment ID: {}", usersLikeCommentId.getUserId(), usersLikeCommentId.getCommentId());
                PostComment postComment = commentRepository.findById(objectUserDTO.getObjectId()).orElse(null);
                postInfoService.UpdatePostCommentReplyCount(postComment.getPostId());
                //Update comment count
                commentInfoService.UpdateCommentLikeCount(objectUserDTO.getObjectId());
            }
        } catch (Exception e) {
            logger.error("Error setting user like comment: {}", e.getMessage(), e);
        }
    }

    public UsersLikeComment CreateUserLikeComment(UsersLikeCommentId usersLikeCommentId) {
        logger.info("Creating user like comment for user ID: {}, comment ID: {}", usersLikeCommentId.getUserId(), usersLikeCommentId.getCommentId());

        UsersLikeComment usersLikeComment = new UsersLikeComment();
        usersLikeComment.setId(usersLikeCommentId);
        usersLikeComment.setLikeStatus(false);
        usersLikeComment.setCreatedAt(Instant.now());
        usersLikeComment.setModifiedAt(Instant.now());
        logger.info("Created user like comment for user ID: {}, comment ID: {}", usersLikeCommentId.getUserId(), usersLikeCommentId.getCommentId());
        return usersLikeComment;
    }

    @Transactional
    public void SetUserLikeReply(List<ObjectUserDTO> objectUserDTOList) {
        logger.info("Setting user like reply");
        try {
            for (ObjectUserDTO objectUserDTO : objectUserDTOList) {
                logger.info("Setting user like reply for user ID: {}, reply ID: {}", objectUserDTO.getUserId(), objectUserDTO.getObjectId());
                UsersLikeReplyId usersLikeReplyId = new UsersLikeReplyId();
                usersLikeReplyId.setReplyId(objectUserDTO.getObjectId());
                usersLikeReplyId.setUserId(objectUserDTO.getUserId());

                UsersLikeReply usersLikeReply = userLikeReplyRepository.findById(usersLikeReplyId)
                        .orElseGet(() -> CreateUserLikeReply(usersLikeReplyId));
                usersLikeReply.setLikeStatus(!usersLikeReply.getLikeStatus());
                usersLikeReply.setModifiedAt(objectUserDTO.getCreatedAt());
                userLikeReplyRepository.save(usersLikeReply);
                logger.info("User like reply saved successfully for user ID: {}, reply ID: {}", usersLikeReplyId.getUserId(), usersLikeReplyId.getReplyId());
                List<Map<String, Object>> postInfo = replyRepository.findPostIdByReplyId(objectUserDTO.getObjectId());
                Long postId = ((BigInteger)postInfo.get(0).get("post_id")).longValue();
                postInfoService.UpdatePostCommentReplyCount(postId);
                //Update reply count
                replyInfoService.UpdateReplyLikeCount(objectUserDTO.getObjectId());
            }
        } catch (Exception e) {
            logger.error("Error setting user like reply: {}", e.getMessage(), e);
        }

    }

    public UsersLikeReply CreateUserLikeReply(UsersLikeReplyId usersLikeReplyId) {
        logger.info("Creating user like reply for user ID: {}, reply ID: {}", usersLikeReplyId.getUserId(), usersLikeReplyId.getReplyId());

        UsersLikeReply usersLikeReply = new UsersLikeReply();
        usersLikeReply.setId(usersLikeReplyId);
        usersLikeReply.setLikeStatus(false);
        usersLikeReply.setCreatedAt(Instant.now());
        usersLikeReply.setModifiedAt(Instant.now());

        logger.info("Created user like reply for user ID: {}, reply ID: {}", usersLikeReplyId.getUserId(), usersLikeReplyId.getReplyId());
        return usersLikeReply;
    }


    public UserFavoritePostVO GetUserFavoritePostStatus(ObjectUserDTO objectUserDTO) {
        logger.info("Getting user favorite post status for user ID: {}, post ID: {}", objectUserDTO.getUserId(), objectUserDTO.getObjectId());
        try {
            UsersFavoritePostId usersFavoritePostId = new UsersFavoritePostId();
            usersFavoritePostId.setPostId(objectUserDTO.getObjectId());
            usersFavoritePostId.setUserId(objectUserDTO.getUserId());
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
        userFavoritePostVO.setUserId(usersFavoritePost.getId().getUserId().toString());
        userFavoritePostVO.setPostId(usersFavoritePost.getId().getPostId().toString());
        userFavoritePostVO.setLikeStatus(usersFavoritePost.getLikeStatus());
        userFavoritePostVO.setSaveStatus(usersFavoritePost.getSaveStatus());

        logger.info("Transferred user favorite post status to VO for user ID: {}, post ID: {}", usersFavoritePost.getId().getUserId(), usersFavoritePost.getId().getPostId());
        return userFavoritePostVO;
    }
}

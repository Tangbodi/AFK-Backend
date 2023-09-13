package com.example.demo.Service.UserLikeSave;

import com.example.demo.Mapper.Repository.*;
import com.example.demo.Model.DTO.ObjectUserDTO;
import com.example.demo.Model.Entity.*;
import com.example.demo.Model.VO.ShowSavedPostVO;
import com.example.demo.Service.Comments.CommentInfoService;
import com.example.demo.Service.Posts.PostInfoService;
import com.example.demo.Service.Replies.ReplyInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
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

    @Async("MultiExecutor")
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
                        .orElseGet(() -> CreateUserLikePost(usersFavoritePostId));
                usersFavoritePost.setLikeStatus(objectUserDTO.getStatus() == 1);
                usersFavoritePost.setModifiedAt(Instant.now());
                userFavoritePostRepository.save(usersFavoritePost);
                logger.info("User like post saved successfully for user ID: {}, post ID: {}", usersFavoritePostId.getUserId(), usersFavoritePostId.getPostId());
            }

        } catch (Exception e) {
            logger.error("Error setting user like post: {}", e.getMessage(), e);
        }
    }
    @Transactional
    private static UsersFavoritePost CreateUserLikePost(UsersFavoritePostId usersFavoritePostId) {
        logger.info("Creating user like post for user ID: {}, post ID: {}", usersFavoritePostId.getUserId(), usersFavoritePostId.getPostId());

        UsersFavoritePost usersFavoritePost = new UsersFavoritePost();
        usersFavoritePost.setId(usersFavoritePostId);
        usersFavoritePost.setSaveStatus(false);
        usersFavoritePost.setLikeStatus(false);
        usersFavoritePost.setCreatedAt(Instant.now());
        usersFavoritePost.setModifiedAt(Instant.now());

        logger.info("Created user like post for user ID: {}, post ID: {}", usersFavoritePostId.getUserId(), usersFavoritePostId.getPostId());
        return usersFavoritePost;
    }
    @Async("MultiExecutor")
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
                usersFavoritePost.setSaveStatus(objectUserDTO.getStatus() == 1);
                usersFavoritePost.setModifiedAt(Instant.now());
                userFavoritePostRepository.save(usersFavoritePost);
                logger.info("User save post saved successfully for user ID: {}, post ID: {}", usersFavoritePostId.getUserId(), usersFavoritePostId.getPostId());
            }
        } catch (Exception e) {
            logger.error("Error setting user save post: {}", e.getMessage(), e);
        }

    }
    @Transactional
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
    @Async("MultiExecutor")
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
                usersLikeComment.setLikeStatus(objectUserDTO.getStatus() == 1);
                usersLikeComment.setModifiedAt(Instant.now());
                userLikeCommentRepository.save(usersLikeComment);
                logger.info("User like comment saved successfully for user ID: {}, comment ID: {}", usersLikeCommentId.getUserId(), usersLikeCommentId.getCommentId());
            }
        } catch (Exception e) {
            logger.error("Error setting user like comment: {}", e.getMessage(), e);
        }
    }
    @Transactional
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
    @Async("MultiExecutor")
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
                usersLikeReply.setLikeStatus(objectUserDTO.getStatus() == 1);
                usersLikeReply.setModifiedAt(Instant.now());
                userLikeReplyRepository.save(usersLikeReply);
                logger.info("User like reply saved successfully for user ID: {}, reply ID: {}", usersLikeReplyId.getUserId(), usersLikeReplyId.getReplyId());
                List<Map<String, Object>> postInfo = replyRepository.findPostIdByReplyId(objectUserDTO.getObjectId());
            }
        } catch (Exception e) {
            logger.error("Error setting user like reply: {}", e.getMessage(), e);
        }

    }
    @Transactional
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


    public List<ShowSavedPostVO> GetSavedPostByUserId(ObjectUserDTO objectUserDTO) {
        logger.info("Getting user favorite post status for user ID: {}, post ID: {}", objectUserDTO.getUserId(), objectUserDTO.getObjectId());
        try {
            List<Map<String, Object>> savedPosts = userFavoritePostRepository.findAllSavedPostsByUserId(objectUserDTO.getUserId());
            if (!savedPosts.isEmpty()) {
                logger.info("All saved posts found for user ID: {}", objectUserDTO.getUserId());
                return TransferToUserFavoritePostVO(savedPosts, objectUserDTO);
            } else {
                logger.info("No saved posts found for user ID: {}", objectUserDTO.getUserId());
                return null;
            }
        } catch (Exception e) {
            logger.error("Error getting saved posts: {}", e.getMessage(), e);
            return null;
        }
    }

    private static List<ShowSavedPostVO> TransferToUserFavoritePostVO( List<Map<String, Object>> savedPosts, ObjectUserDTO objectUserDTO) {
        logger.info("Transferring saved posts to VO for user ID: {}", objectUserDTO.getUserId());
        List<ShowSavedPostVO> showSavedPostVOList = new ArrayList<>();
        for(Map<String, Object> savedPost : savedPosts){
            ShowSavedPostVO showSavedPostVO = new ShowSavedPostVO();
            showSavedPostVO.setPostId(savedPost.get("post_id").toString());
            showSavedPostVO.setTitle((String) savedPost.get("title"));
            showSavedPostVO.setView((Integer) savedPost.get("view"));
            showSavedPostVO.setCommentReply((Integer) savedPost.get("comment_reply"));
            showSavedPostVO.setLike((Integer) savedPost.get("like"));
            Timestamp timestamp = (Timestamp) savedPost.get("created_at");
            showSavedPostVO.setCreatedAt(timestamp.toInstant());
            showSavedPostVOList.add(showSavedPostVO);
        }
        logger.info("Transferred saved posts to VO for user ID: {}", objectUserDTO.getUserId());
        return showSavedPostVOList;
    }
}

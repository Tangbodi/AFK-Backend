package com.example.demo.Service.Redis;

import com.example.demo.Constant.Enum.ObjectNameEnum;
import com.example.demo.Mapper.Repository.*;
import com.example.demo.Model.DTO.MessageDTO;
import com.example.demo.Model.DTO.UserLikeSaveDTO;
import com.example.demo.Model.Entity.*;
import com.example.demo.Service.Message.MessageService;
import com.example.demo.Service.UserLikeSave.UserLikeSaveService;
import com.example.demo.Service.UserSettings.UserSettingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.time.Instant;

@Service
public class RedisUserLikeSaveService {
    private static final Logger logger = LoggerFactory.getLogger(RedisUserLikeSaveService.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final String UPDATE = "UPDATE_";
    @Autowired
    private JedisPool jedisPool;
    @Autowired
    private RedisService redisService;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private PostGameMapRepository postGameMapRepository;
    @Autowired
    private UserSettingService userSettingService;
    @Autowired
    private MessageService messageService;
    @Autowired
    private ReplyRepository replyRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private PostUserMapRepository postUserMapRepository;
    @Autowired
    private UserLikeSaveService userLikeSaveService;

    public void HandleLikeSaveStrategy(UserLikeSaveDTO userLikeSaveDTO) throws InterruptedException {
        logger.info("Handling like save strategy");
        //post_like/comment_like/reply_like/post_save/game_save/0/1/2/3/4
        String typeName = ObjectNameEnum.GetTypeName(userLikeSaveDTO.getTypeId());
        //postId/commentId/replyId
        Long objectId = userLikeSaveDTO.getObjectId();
        //post_like:::objectId
        String key = typeName + ":::" + objectId;
        String hashKey = String.valueOf(userLikeSaveDTO.getUserId());
        String value = String.valueOf(userLikeSaveDTO.getStatus());
        String updateTypeName = UPDATE+typeName;
        String updateKey = updateTypeName + ":::" + objectId;
        //whatever the status is, add to set
        //if the Type name doesn't exist then add to set
        //for update status of like and save LIMITED TIME
        if (!redisService.MemberExists(typeName, objectId)) {
            //post_like/comment_like/reply_like/post_save
            redisService.AddSet(typeName, objectId);
            //like/save
//                redisLikeSaveService.AddSet(typeName, objectId);
        } else {
            //
        }
        //For update count of like and save
        if(!redisService.MemberExists(updateTypeName, objectId)){
            redisService.AddSet(updateTypeName, objectId);
        } else {
            //
        }
        //postId/commentId/replyId -> userId -> 1/0
        redisService.AddTimeLimitedHashSet(key, hashKey, value);
        redisService.AddHashSet(updateKey, hashKey, value);
        //update status from Redis to DB
        UpdateUserLikeSaveStatusFromRedisToDB(key, userLikeSaveDTO);
    }

    @Async("MultiExecutor")
    public void UpdateUserLikeSaveStatusFromRedisToDB(String key, UserLikeSaveDTO userLikeSaveDTO) throws InterruptedException {
        logger.info("Updating user like save status from Redis to DB");
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String userId = userLikeSaveDTO.getUserId().toString();
            //1/0
            String userLikeSaveStatus_json = jedis.hget(key, userId);
            if (userLikeSaveStatus_json != null) {
                Integer likeSaveStatus = objectMapper.readValue(userLikeSaveStatus_json, Integer.class);
                switch (userLikeSaveDTO.getTypeId()) {
                    case 0:
                        userLikeSaveService.SetUserLikePost(userLikeSaveDTO, likeSaveStatus);
                        break;
                    case 1:
                        userLikeSaveService.SetUserLikeComment(userLikeSaveDTO, likeSaveStatus);
                        break;
                    case 2:
                        userLikeSaveService.SetUserLikeReply(userLikeSaveDTO, likeSaveStatus);
                        break;
                    case 3:
                        userLikeSaveService.SetUserSavePost(userLikeSaveDTO, likeSaveStatus);
                        break;
                    default:
                        break;
                }
            } else {
                logger.info("No user like save status found");
            }
        } catch (Exception e) {
            logger.error("Failed to update user like save status from Redis to DB: {}", e.getMessage(), e);
        } finally {
            if (null != jedis) {
                logger.info("Closing the jedis connection:::");
                jedis.close();
            }
        }
    }

    public void HandleLikeSaveMentionStrategy(UserLikeSaveDTO userLikeSaveDTO) {
        logger.info("Handling like save mention strategy");
        //if user likes comment
        int typeId = userLikeSaveDTO.getTypeId();
        Long objectId = userLikeSaveDTO.getObjectId();
        MessageDTO messageDTO = new MessageDTO();
        switch (typeId) {
            //POST_LIKE
            case 0:
                logger.info("case 0");
                PostsUsersMap postsUsersMap = postUserMapRepository.findByPostId(objectId).orElse(null);
                Post post = postRepository.findById(objectId).orElse(null);
                Long postAuthorId = postsUsersMap.getId().getUserId();
                if (!postAuthorId.equals(userLikeSaveDTO.getUserId()) && userSettingService.CheckLikeOnPostMention(postAuthorId)) {
                    String postTitle = post.getTitle();
                    messageDTO.setPostId(objectId);
                    //if user likes post then commentReplyId = 0
                    messageDTO.setCommentReplyId(0L);
                    messageDTO.setToUid(postAuthorId);
                    messageDTO.setFromUid(userLikeSaveDTO.getUserId());
                    messageDTO.setContent(postTitle);
                    messageDTO.setTypeId(userLikeSaveDTO.getTypeId());
                    messageDTO.setCreatedAt(Instant.now());
                    messageService.SaveMessage(messageDTO);
                } else {
                    //
                }
                break;
            //COMMENT_LIKE
            case 1:
                logger.info("case 1");
                PostComment postComment = commentRepository.findById(userLikeSaveDTO.getObjectId()).orElse(null);
                Long commentAuthorId = postComment.getFromUid();
                Long postId = postComment.getPostId();
                if (!commentAuthorId.equals(userLikeSaveDTO.getUserId()) && userSettingService.CheckLikeOnCommentMention(commentAuthorId)) {
                    String commentContent = postComment.getContent();
                    //set mention message
                    messageDTO.setPostId(postId);
                    messageDTO.setCommentReplyId(userLikeSaveDTO.getObjectId());
                    messageDTO.setToUid(commentAuthorId);
                    messageDTO.setFromUid(userLikeSaveDTO.getUserId());
                    messageDTO.setContent(commentContent);
                    messageDTO.setTypeId(userLikeSaveDTO.getTypeId());
                    messageDTO.setCreatedAt(Instant.now());
                    messageService.SaveMessage(messageDTO);
                } else {
                    //
                }
                break;
            //REPLY_LIKE
            case 2:
                logger.info("case 2");
                PostReply postReply = replyRepository.findById(userLikeSaveDTO.getObjectId()).orElse(null);
                Long replyAuthorId = postReply.getFromUid();
                Long commentId = postReply.getCommentId();
                postComment = commentRepository.findById(commentId).orElse(null);
                postId = postComment.getPostId();
                if (!replyAuthorId.equals(userLikeSaveDTO.getUserId()) && userSettingService.CheckLikeOnCommentMention(replyAuthorId)) {
                    String replyContent = postReply.getContent();
                    messageDTO.setPostId(postId);
                    messageDTO.setCommentReplyId(userLikeSaveDTO.getObjectId());
                    messageDTO.setToUid(replyAuthorId);
                    messageDTO.setFromUid(userLikeSaveDTO.getUserId());
                    messageDTO.setContent(replyContent);
                    messageDTO.setTypeId(userLikeSaveDTO.getTypeId());
                    messageDTO.setCreatedAt(Instant.now());
                    messageService.SaveMessage(messageDTO);
                } else {
                    //
                }
                break;
            //POST_SAVE
            case 3:
                logger.info("case 3");
                postsUsersMap = postUserMapRepository.findByPostId(objectId).orElse(null);
                post = postRepository.findById(objectId).orElse(null);
                postAuthorId = postsUsersMap.getId().getUserId();
                if (!postAuthorId.equals(userLikeSaveDTO.getUserId()) && userSettingService.CheckSaveOnPostMention(postAuthorId)) {
                    String postTitle = post.getTitle();
                    messageDTO.setPostId(objectId);
                    //if user saves post then commentReplyId = 0
                    messageDTO.setCommentReplyId(0L);
                    messageDTO.setToUid(postAuthorId);
                    messageDTO.setFromUid(userLikeSaveDTO.getUserId());
                    messageDTO.setContent(postTitle);
                    messageDTO.setTypeId(userLikeSaveDTO.getTypeId());
                    messageDTO.setCreatedAt(Instant.now());
                    messageService.SaveMessage(messageDTO);
                } else {
                    //
                }
                break;
            default:
                break;
        }
    }
}

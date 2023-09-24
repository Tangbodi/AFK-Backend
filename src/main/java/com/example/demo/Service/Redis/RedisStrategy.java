package com.example.demo.Service.Redis;

import com.example.demo.Constant.Enum.CountNameEnum;
import com.example.demo.Constant.Enum.ObjectNameEnum;
import com.example.demo.Constant.Enum.StatusEnum;
import com.example.demo.Mapper.Repository.CommentRepository;
import com.example.demo.Mapper.Repository.PostRepository;
import com.example.demo.Mapper.Repository.ReplyRepository;
import com.example.demo.Model.DTO.*;
import com.example.demo.Model.Entity.Post;
import com.example.demo.Model.Entity.PostComment;
import com.example.demo.Model.Entity.PostReply;
import com.example.demo.Model.VO.GameIconVO;
import com.example.demo.Model.VO.UserFavoriteGameVO;
import com.example.demo.Service.EmailValidation.ProcessEmailService;
import com.example.demo.Service.Message.MessageService;
import com.example.demo.Service.UserFavoriteGame.UserFavoriteGameService;
import com.example.demo.Service.UsersInfo.UserInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.util.List;

@Component
public class RedisStrategy {
    private static final Logger logger = LoggerFactory.getLogger(RedisStrategy.class);
    private static final String SAVED_GAME = ObjectNameEnum.SAVED_GAME_SET.getTypeName();
    @Autowired
    private RedisService redisService;
    @Autowired
    protected RedisMessageService redisMessageService;
    @Autowired
    protected ProcessEmailService processEmailService;
    @Autowired
    private RedisGameIconService redisGameIconService;
    @Autowired
    private UserFavoriteGameService userFavoriteGameService;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private ReplyRepository replyRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private UserInfoService userInfoService;
    @Autowired
    private MessageService messageService;

    public void UserRegistrationStrategy(UserRegisterDTO userRegisterDTO) {
        logger.info("Start UserRegistrationStrategy");
        processEmailService.ProcessRegistrationEmailValidation(userRegisterDTO);
    }

    public void UserFavoriteGameStrategy(UserLikeSaveDTO userLikeSaveDTO) throws IOException {
        logger.info("Start UserFavoriteGameStrategy");
        Long userId = userLikeSaveDTO.getUserId();

        Short gameId = userLikeSaveDTO.getObjectId().shortValue();
        String typeName = ObjectNameEnum.GetTypeName(userLikeSaveDTO.getTypeId());
        String key = typeName + ":::" + userId;
        Integer status = userLikeSaveDTO.getStatus();
        if (!redisService.MemberExists(key, userId)) {
            logger.info("User favorite game list doesn't exist in Redis, getting from DB");
            userFavoriteGameService.GetUserFavoriteGames(userId);
        }
        //Get user favorite game list from Redis to update
        List<UserFavoriteGameVO> userFavoriteGameVOList = redisGameIconService.GetUserFavoriteGameCache(SAVED_GAME + ":::" + userId, userId);
        //Get all game icons list from Redis
        List<GameIconVO> gameIconVOList = redisGameIconService.GetAllGameIconsCache();
        //Find game in all game icons list
        GameIconVO gameIconVO = gameIconVOList.stream()
                .filter(gameIconVO1 -> gameIconVO1.getGameId().equals(gameId))
                .findFirst()
                .orElse(null);
        //if status is 1, add game to user favorite game list
        if (status == StatusEnum.TRUE.getCode()) {
            logger.info("Adding game to user favorite game list");
            //Add game to UserFavoriteGameVO
            UserFavoriteGameVO userFavoriteGameVO = new UserFavoriteGameVO();
            userFavoriteGameVO.setGenreId(gameIconVO.getGenreId());
            userFavoriteGameVO.setGameId(gameIconVO.getGameId());
            userFavoriteGameVO.setGameName(gameIconVO.getGameName());
            userFavoriteGameVO.setIconUrl(gameIconVO.getIconUrl());
            userFavoriteGameVOList.add(userFavoriteGameVO);
            redisGameIconService.SetUserFavoriteGameCache(key, userId, userFavoriteGameVOList);
            logger.info("Added game to UserFavoriteGameVO");
        } else {
            //Remove game from UserFavoriteGameVO
            logger.info("Removing game from user favorite game list");
            userFavoriteGameVOList.removeIf(userFavoriteGameVO -> userFavoriteGameVO.getGameId().equals(gameId));
            redisGameIconService.SetUserFavoriteGameCache(key, userId, userFavoriteGameVOList);
            logger.info("Removed game from user favorite game list");
        }
    }

    public void LikeSaveStrategy(UserLikeSaveDTO userLikeSaveDTO) {
        logger.info("Start LikeSaveStrategy");
        //post_like/comment_like/reply_like/post_save/game_save/0/1/2/3/4
        String typeName = ObjectNameEnum.GetTypeName(userLikeSaveDTO.getTypeId());
        //postId/commentId/replyId
        Long objectId = userLikeSaveDTO.getObjectId();
        //post_like:::objectId
        String key = typeName + ":::" + objectId;
        String hashKey = String.valueOf(userLikeSaveDTO.getUserId());
        String value = String.valueOf(userLikeSaveDTO.getStatus());
        //whatever the status is, add to set
        //if the Type name doesn't exist then add to set
        if (!redisService.MemberExists(typeName, objectId)) {
            //post_like/comment_like/reply_like/post_save
            redisService.AddSet(typeName, objectId);
            //like/save
//                redisLikeSaveService.AddSet(typeName, objectId);
        } else {
            //
        }
        //postId/commentId/replyId -> userId -> createdAt
        redisService.AddHashSet(key, hashKey, value);
    }

    public void LikeSaveMentionStrategy(UserLikeSaveDTO userLikeSaveDTO) {
        logger.info("Start LikeSaveMentionStrategy");
        //if user likes comment
        int typeId = userLikeSaveDTO.getTypeId();
        Long objectId = userLikeSaveDTO.getObjectId();
        MessageDTO messageDTO = new MessageDTO();
        if (typeId == ObjectNameEnum.COMMENT_LIKE_SET.getTypeCode()) {
            //find comment content and author id by commentId
            PostComment postComment = commentRepository.findById(objectId).orElse(null);
            Long commentAuthorId = postComment.getFromUid();
            String commentContent = postComment.getContent();
            //set mention message
            messageDTO.setToUid(commentAuthorId);
            messageDTO.setFromUid(userLikeSaveDTO.getUserId());
            messageDTO.setContent(commentContent);
        } else if (typeId == ObjectNameEnum.REPLY_LIKE_SET.getTypeCode()) {
            //find reply content and author id by replyId
            PostReply postReply = replyRepository.findById(objectId).orElse(null);
            Long replyAuthorId = postReply.getFromUid();
            String replyContent = postReply.getContent();
            messageDTO.setToUid(replyAuthorId);
            messageDTO.setFromUid(userLikeSaveDTO.getUserId());
            messageDTO.setContent(replyContent);
        } else if (typeId == ObjectNameEnum.POST_LIKE_SET.getTypeCode()) {
            //find post content and author id by postId
            Post post = postRepository.findById(objectId).orElse(null);
            Long postAuthorId = post.getId();
            String postTitle = post.getTitle();
            messageDTO.setToUid(postAuthorId);
            messageDTO.setFromUid(userLikeSaveDTO.getUserId());
            messageDTO.setContent(postTitle);
        } else if (typeId == ObjectNameEnum.POST_SAVE_SET.getTypeCode()) {
            //find post content and author id by postId
            Post post = postRepository.findById(objectId).orElse(null);
            Long postAuthorId = post.getId();
            String postTitle = post.getTitle();
            messageDTO.setToUid(postAuthorId);
            messageDTO.setFromUid(userLikeSaveDTO.getUserId());
            messageDTO.setContent(postTitle);
        }
        messageDTO.setCommentReplyId(objectId);
        messageDTO.setTypeId(userLikeSaveDTO.getTypeId());
        messageDTO.setCreatedAt(Instant.now());
        if (!messageDTO.getFromUid().equals(messageDTO.getToUid())) {
            messageService.SaveMessage(messageDTO);
        } else {
            //
        }
    }

    public void CommentCountStrategy(CommentReplyDTO commentReplyDTO) {
        logger.info("Start CommentCountStrategy");
        Long postId = commentReplyDTO.getPostId();
        String countName = CountNameEnum.COMMENT_COUNT.getCountName();
        String key = countName + ":::" + postId;
        String hashKey = String.valueOf(commentReplyDTO.getCommentId());
        String value = String.valueOf(commentReplyDTO.getFromUid());
        if (!redisService.MemberExists(countName, postId)) {
            //COMMENT_COUNT
            redisService.AddSet(countName, postId);
        } else {
            //
        }
        //postId -> commentId -> fromUid
        redisService.AddHashSet(key, hashKey, value);
    }

    public void ReplyCountStrategy(CommentReplyDTO commentReplyDTO) {
        logger.info("Start ReplyCountStrategy");
        Long postId = commentReplyDTO.getPostId();
        String countName = CountNameEnum.REPLY_COUNT.getCountName();
        String key = countName + ":::" + postId;
        String hashKey = String.valueOf(commentReplyDTO.getReplyId());
        String value = String.valueOf(commentReplyDTO.getFromUid());
        if (!redisService.MemberExists(countName, postId)) {
            //REPLY_COUNT
            redisService.AddSet(countName, postId);
        } else {
            //
        }
        //postId -> replyId -> fromUid
        redisService.AddHashSet(key, hashKey, value);
    }

    public void MessageMentionStrategy(Long userId) {
        logger.info("Start MessageMentionStrategy");
        redisMessageService.GetUnreadMessageByUserId(userId);
    }

    public void UpdateEmailStrategy(EmailDTO emailDTO) {
        logger.info("Start UpdateEmailStrategy");
        processEmailService.ProcessUpdateEmailValidation(emailDTO);
    }

    public void ForgotPasswordStrategy(EmailDTO emailDTO) {
        logger.info("Start ForgotPasswordStrategy");
        processEmailService.ProcessForgotPasswordEmailValidation(emailDTO);
    }

}

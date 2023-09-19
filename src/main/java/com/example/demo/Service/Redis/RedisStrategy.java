package com.example.demo.Service.Redis;

import com.example.demo.Constant.Enum.CountNameEnum;
import com.example.demo.Constant.Enum.ObjectNameEnum;
import com.example.demo.Constant.Enum.StatusEnum;
import com.example.demo.Model.DTO.*;
import com.example.demo.Model.VO.GameIconVO;
import com.example.demo.Model.VO.MessageVO;
import com.example.demo.Model.VO.UserFavoriteGameVO;
import com.example.demo.Service.EmailValidation.ProcessEmailService;
import com.example.demo.Service.Games.GameGenreMapService;
import com.example.demo.Service.UserFavoriteGame.UserFavoriteGameService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Map;

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

    public void UserRegistrationStrategy(UserRegisterDTO userRegisterDTO){
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
        if(!redisService.MemberExists(key,userId)){
            logger.info("User favorite game list doesn't exist in Redis, getting from DB");
            userFavoriteGameService.GetUserFavoriteGames(userId);
        }
        //Get user favorite game list from Redis to update
        List<UserFavoriteGameVO> userFavoriteGameVOList = redisGameIconService.GetUserFavoriteGameCache(SAVED_GAME + ":::" + userId,userId);
        //Get all game icons list from Redis
        List<GameIconVO> gameIconVOList = redisGameIconService.GetAllGameIconsCache();
        //Find game in all game icons list
        GameIconVO gameIconVO = gameIconVOList.stream()
                .filter(gameIconVO1 -> gameIconVO1.getGameId().equals(gameId))
                .findFirst()
                .orElse(null);
        //if status is 1, add game to user favorite game list
        if(status == StatusEnum.TRUE.getCode()){
            logger.info("Adding game to user favorite game list");
            //Add game to UserFavoriteGameVO
            UserFavoriteGameVO userFavoriteGameVO = new UserFavoriteGameVO();
            userFavoriteGameVO.setGenreId(gameIconVO.getGenreId());
            userFavoriteGameVO.setGameId(gameIconVO.getGameId());
            userFavoriteGameVO.setGameName(gameIconVO.getGameName());
            userFavoriteGameVO.setIconUrl(gameIconVO.getIconUrl());
            userFavoriteGameVOList.add(userFavoriteGameVO);
            redisGameIconService.SetUserFavoriteGameCache(key,userId,userFavoriteGameVOList);
            logger.info("Added game to UserFavoriteGameVO");
        } else {
            //Remove game from UserFavoriteGameVO
          logger.info("Removing game from user favorite game list");
            userFavoriteGameVOList.removeIf(userFavoriteGameVO -> userFavoriteGameVO.getGameId().equals(gameId));
            redisGameIconService.SetUserFavoriteGameCache(key,userId,userFavoriteGameVOList);
            logger.info("Removed game from user favorite game list");
        }
    }
    public void LikeSaveStrategy(UserLikeSaveDTO userLikeSaveDTO) {
        logger.info("Start LikeSaveStrategy");
        // 1 or 0
        Integer status = userLikeSaveDTO.getStatus();
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
    public void CommentCountStrategy(CommentReplyDTO commentReplyDTO){
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
    public void ReplyCountStrategy(CommentReplyDTO commentReplyDTO){
        logger.info("Start ReplyCountStrategy");
        Long postId = commentReplyDTO.getPostId();
        String countName = CountNameEnum.REPLY_COUNT.getCountName();
        String key = countName + ":::" + postId;
        String hashKey = String.valueOf(commentReplyDTO.getReplyId());
        String value = String.valueOf(commentReplyDTO.getFromUid());
        if(!redisService.MemberExists(countName,postId)) {
            //REPLY_COUNT
            redisService.AddSet(countName, postId);
        } else {
            //
        }
        //postId -> replyId -> fromUid
        redisService.AddHashSet(key, hashKey, value);
    }
    public void MessageMentionStrategy(MessageVO messageVO){
        logger.info("Start MessageMentionStrategy");
        redisMessageService.SetUnreadMessage(messageVO);
    }
    public void UpdateEmailStrategy(EmailDTO emailDTO){
        logger.info("Start UpdateEmailStrategy");
        processEmailService.ProcessUpdateEmailValidation(emailDTO);
    }
    public void ForgotPasswordStrategy(EmailDTO emailDTO){
        logger.info("Start ForgotPasswordStrategy");
        processEmailService.ProcessForgotPasswordEmailValidation(emailDTO);
    }

}

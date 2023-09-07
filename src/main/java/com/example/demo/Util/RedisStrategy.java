package com.example.demo.Util;

import com.example.demo.Constant.Enum.CountNameEnum;
import com.example.demo.Constant.Enum.ObjectNameEnum;
import com.example.demo.Constant.Enum.StatusEnum;
import com.example.demo.Model.DTO.CommentReplyDTO;
import com.example.demo.Model.DTO.EmailDTO;
import com.example.demo.Model.DTO.UserLikeSaveDTO;
import com.example.demo.Model.DTO.UserRegisterDTO;
import com.example.demo.Model.VO.MessageVO;
import com.example.demo.Service.EmailValidation.ProcessEmailService;
import com.example.demo.Service.Redis.RedisLikeSaveService;
import com.example.demo.Service.Redis.RedisMessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RedisStrategy {
    private static final Logger logger = LoggerFactory.getLogger(RedisStrategy.class);

    @Autowired
    protected RedisLikeSaveService redisLikeSaveService;
    @Autowired
    protected RedisMessageService redisMessageService;
    @Autowired
    protected ProcessEmailService processEmailService;

    public void UserRegistrationStrategy(UserRegisterDTO userRegisterDTO){
        logger.info("Start UserRegistrationStrategy");
        processEmailService.ProcessRegistrationEmailValidation(userRegisterDTO);
    }
    public void LikeSaveStrategy(UserLikeSaveDTO userLikeSaveDTO) {
        logger.info("Start LikeSaveStrategy");
        // 1 or 0
        Integer status = userLikeSaveDTO.getStatus();
        //post_like/comment_like/reply_like/post_save/0/1/2/3
        String typeName = ObjectNameEnum.GetTypeName(userLikeSaveDTO.getTypeId());
        //postId/commentId/replyId
        Long objectId = userLikeSaveDTO.getObjectId();
        //post_like:::objectId
        String key = typeName + ":::" + objectId;
        String hashKey = String.valueOf(userLikeSaveDTO.getUserId());
        String value = String.valueOf(userLikeSaveDTO.getCreatedAt());

        //if status is 1
        if (status == StatusEnum.TRUE.getCode()) {
            //if the Type name doesn't exist then add to set
            if (!redisLikeSaveService.MemberExists(typeName, objectId)) {
                //post_like/comment_like/reply_like/post_save
                redisLikeSaveService.AddSet(typeName, objectId);
                //like/save
//                redisLikeSaveService.AddSet(typeName, objectId);
            } else {
                //
            }
            //postId/commentId/replyId -> userId -> createdAt
            redisLikeSaveService.AddHashSet(key, hashKey, value);
        } else {
            //if status is 0, remove from hash set
            redisLikeSaveService.DeleteMember(key, hashKey);
            if (redisLikeSaveService.NumOfMembers(key) == 0L) {
                redisLikeSaveService.RemoveHashSet(typeName, objectId);
            } else {
                //
            }
        }
    }
    public void CommentCountStrategy(CommentReplyDTO commentReplyDTO){
        logger.info("Start CommentCountStrategy");
        Long postId = commentReplyDTO.getPostId();
        String countName = CountNameEnum.COMMENT_COUNT.getCountName();
        String key = countName + ":::" + postId;
        String hashKey = String.valueOf(commentReplyDTO.getCommentId());
        String value = String.valueOf(commentReplyDTO.getFromUid());
        if (!redisLikeSaveService.MemberExists(countName, postId)) {
            //COMMENT_COUNT
            redisLikeSaveService.AddSet(countName, postId);
        } else {
            //
        }
        //postId -> commentId -> fromUid
        redisLikeSaveService.AddHashSet(key, hashKey, value);
    }
    public void ReplyCountStrategy(CommentReplyDTO commentReplyDTO){
        logger.info("Start ReplyCountStrategy");
        Long postId = commentReplyDTO.getPostId();
        String countName = CountNameEnum.REPLY_COUNT.getCountName();
        String key = countName + ":::" + postId;
        String hashKey = String.valueOf(commentReplyDTO.getReplyId());
        String value = String.valueOf(commentReplyDTO.getFromUid());
        if(!redisLikeSaveService.MemberExists(countName,postId)) {
            //REPLY_COUNT
            redisLikeSaveService.AddSet(countName, postId);
        } else {
            //
        }
        //postId -> replyId -> fromUid
        redisLikeSaveService.AddHashSet(key, hashKey, value);
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

package com.example.demo.Service.Redis;

import com.example.demo.Constant.Enum.ObjectNameEnum;
import com.example.demo.Mapper.Repository.CommentRepository;
import com.example.demo.Mapper.Repository.PostRepository;
import com.example.demo.Mapper.Repository.ReplyRepository;
import com.example.demo.Model.DTO.MessageDTO;
import com.example.demo.Model.DTO.UserLikeSaveDTO;
import com.example.demo.Model.Entity.Post;
import com.example.demo.Model.Entity.PostComment;
import com.example.demo.Model.Entity.PostReply;
import com.example.demo.Service.Message.MessageService;
import com.example.demo.Service.UsersInfo.UserSettingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class RedisUserLikeSaveService {
    private static final Logger logger = LoggerFactory.getLogger(RedisUserLikeSaveService.class);
    @Autowired
    private RedisService redisService;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private UserSettingService userSettingService;
    @Autowired
    private MessageService messageService;
    @Autowired
    private ReplyRepository replyRepository;
    @Autowired
    private PostRepository postRepository;
    public void HandleLikeSaveStrategy(UserLikeSaveDTO userLikeSaveDTO) {
        logger.info("Handling like save strategy");
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
    public void HandleLikeSaveMentionStrategy(UserLikeSaveDTO userLikeSaveDTO){
        logger.info("Handling like save mention strategy");
        //if user likes comment
        int typeId = userLikeSaveDTO.getTypeId();
        Long objectId = userLikeSaveDTO.getObjectId();
        MessageDTO messageDTO = new MessageDTO();
        switch (typeId) {
            //POST_LIKE
            case 0:
                Post post = postRepository.findById(objectId).orElse(null);
                Long postAuthorId = post.getId();
                if(!postAuthorId.equals(userLikeSaveDTO.getUserId()) && userSettingService.CheckLikeOnPostMention(postAuthorId)){
                    String postTitle = post.getTitle();
                    messageDTO.setToUid(postAuthorId);
                    messageDTO.setFromUid(userLikeSaveDTO.getUserId());
                    messageDTO.setContent(postTitle);
                    messageDTO.setCommentReplyId(objectId);
                    messageDTO.setTypeId(userLikeSaveDTO.getTypeId());
                    messageDTO.setCreatedAt(Instant.now());
                    messageService.SaveMessage(messageDTO);
                } else {
                    //
                }
                break;
                //COMMENT_LIKE
            case 1:
                PostComment postComment = commentRepository.findById(objectId).orElse(null);
                Long commentAuthorId = postComment.getFromUid();
                if(!commentAuthorId.equals(userLikeSaveDTO.getUserId()) && userSettingService.CheckLikeOnCommentMention(commentAuthorId)){
                    String commentContent = postComment.getContent();
                    //set mention message
                    messageDTO.setToUid(commentAuthorId);
                    messageDTO.setFromUid(userLikeSaveDTO.getUserId());
                    messageDTO.setContent(commentContent);
                    messageDTO.setCommentReplyId(objectId);
                    messageDTO.setTypeId(userLikeSaveDTO.getTypeId());
                    messageDTO.setCreatedAt(Instant.now());
                    messageService.SaveMessage(messageDTO);
                } else {
                    //
                }
                break;
                //REPLY_LIKE
            case 2:
                PostReply postReply = replyRepository.findById(objectId).orElse(null);
                Long replyAuthorId = postReply.getFromUid();
                if(!replyAuthorId.equals(userLikeSaveDTO.getUserId()) && userSettingService.CheckLikeOnCommentMention(replyAuthorId)){
                    String replyContent = postReply.getContent();
                    messageDTO.setToUid(replyAuthorId);
                    messageDTO.setFromUid(userLikeSaveDTO.getUserId());
                    messageDTO.setContent(replyContent);
                    messageDTO.setCommentReplyId(objectId);
                    messageDTO.setTypeId(userLikeSaveDTO.getTypeId());
                    messageDTO.setCreatedAt(Instant.now());
                    messageService.SaveMessage(messageDTO);
                } else {
                    //
                }
                break;
                //POST_SAVE
            case 3:
                post = postRepository.findById(objectId).orElse(null);
                postAuthorId = post.getId();
                if(!postAuthorId.equals(userLikeSaveDTO.getUserId()) && userSettingService.CheckSaveOnPostMention(postAuthorId)){
                    String postTitle = post.getTitle();
                    messageDTO.setToUid(postAuthorId);
                    messageDTO.setFromUid(userLikeSaveDTO.getUserId());
                    messageDTO.setContent(postTitle);
                    messageDTO.setCommentReplyId(objectId);
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

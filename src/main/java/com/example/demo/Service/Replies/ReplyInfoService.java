package com.example.demo.Service.Replies;

import com.example.demo.Mapper.Repository.PostUserMapRepository;
import com.example.demo.Mapper.Repository.ReplyInfoRepository;
import com.example.demo.Mapper.Repository.UserLikeReplyRepository;
import com.example.demo.Model.DTO.CommentReplyDTO;
import com.example.demo.Model.DTO.ObjectUserDTO;
import com.example.demo.Model.Entity.RepliesInfo;
import com.example.demo.Service.Comments.CommentOnPostMentionService;
import com.example.demo.Service.Comments.CommentService;
import com.example.demo.Service.MQ.MQSender;
import com.example.demo.Service.Message.MessageService;
import com.example.demo.Service.Redis.RedisService;
import com.example.demo.Service.UsersInfo.UserSettingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.jms.JMSException;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

@Service
public class ReplyInfoService {
    private static final Logger logger = LoggerFactory.getLogger(ReplyInfoService.class);
    private static final String MESSAGE_MENTION_KEY = "UNREAD:";
    @Autowired
    private ReplyInfoRepository replyInfoRepository;
    @Autowired
    private UserLikeReplyRepository userLikeReplyRepository;
    @Autowired
    private PostUserMapRepository postUserMapRepository;
    @Autowired
    private ReplyOnCommentMentionService replyOnCommentMentionService;
    @Autowired
    private MessageService messageService;
    @Autowired
    private MQSender mqSender;
    @Autowired
    private RedisService redisService;
    @Autowired
    private CommentOnPostMentionService commentOnPostMentionService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private UserSettingService userSettingService;

    public void CalculateReplyTotalLike(List<Long> replyIds) {
        logger.info("Finding all users like replies list with like status = 1");
        for (Long replyId : replyIds) {
            Map<String, Object> map = userLikeReplyRepository.findReplyTotalLikeByLikeStatus(replyId);
            Integer totalLike = ((BigInteger) map.get("total_like")).intValue();
            UpdateReplyLikeCount(replyId, totalLike);
        }
    }

    private void UpdateReplyLikeCount(Long replyId, Integer totalLike) {
        logger.info("Updating reply like count");
        RepliesInfo repliesInfo = replyInfoRepository.findById(replyId)
                .orElseGet(() -> CreateReplyInfo(replyId));
        repliesInfo.setLike(totalLike);
        replyInfoRepository.save(repliesInfo);
        logger.info("Updated reply like count");
    }

    private static RepliesInfo CreateReplyInfo(Long replyId) {
        logger.info("Creating reply info");
        RepliesInfo repliesInfo = new RepliesInfo();
        repliesInfo.setId(replyId);
        repliesInfo.setLike(0);
        logger.info("Created reply info for reply ID: {}", replyId);
        return repliesInfo;
    }

    @Async("MultiExecutor")
    public void SetReplyMention(CommentReplyDTO commentReplyDTO) throws JMSException {
        Long postAuthorId = postUserMapRepository.findByPostId(commentReplyDTO.getPostId()).get().getId().getUserId();
        Long commentAuthorId = commentService.GetCommentAuthorByCommentId(commentReplyDTO.getCommentId());
        if (!commentReplyDTO.getFromUid().equals(postAuthorId)) {
            //check post author mention setting
            boolean commentOnPostMention = userSettingService.CheckCommentOnPostMention(postAuthorId);
            if (commentOnPostMention) {
                logger.info("Comment on post mention setting is on");
                messageService.SaveMessage(commentReplyDTO, postAuthorId);
                if (redisService.CacheExists(MESSAGE_MENTION_KEY + postAuthorId)) {
                    mqSender.SendMentionMessage(postAuthorId);
                    logger.info("Sent mention message for postAuthorId to MQ");
                } else {
                    //
                }
            } else {
                logger.info("FromUid mention setting is off");
            }
        } else {
            logger.info("FromUid is equal to postAuthorId");
        }
        if (!commentReplyDTO.getFromUid().equals(commentAuthorId) && !commentAuthorId.equals(postAuthorId)) {
            //check comment author mention setting
            boolean mentionOn = userSettingService.CheckReplyOnCommentMention(commentAuthorId);
            if (mentionOn) {
                logger.info("Reply on comment mention setting is on");
                messageService.SaveMessage(commentReplyDTO, commentAuthorId);
                if (redisService.CacheExists(MESSAGE_MENTION_KEY + commentAuthorId)) {
                    mqSender.SendMentionMessage(commentAuthorId);
                    logger.info("Sent mention message for commentAuthorId to MQ");
                } else {
                    //
                }
            } else {
                logger.info("FromUid mention setting is off");
            }
        } else {
            logger.info("FromUid is equal to commentAuthorId or commentAuthorId is equal to postAuthorId");
        }
        //if it is reply on reply
        if (commentReplyDTO.getToReplyId() != null) {
            logger.info("Reply on reply");
            if (!commentReplyDTO.getFromUid().equals(commentReplyDTO.getToUid())
                    && !commentReplyDTO.getToUid().equals(commentAuthorId)
                    && !commentReplyDTO.getToUid().equals(postAuthorId)) {
                //check toUid mention setting
                boolean mentionOn = userSettingService.CheckReplyOnCommentMention(commentReplyDTO.getToUid());
                if (mentionOn) {
                    logger.info("reply on comment mention setting is on");
                    messageService.SaveMessage(commentReplyDTO, commentReplyDTO.getToUid());
                    if (redisService.CacheExists(MESSAGE_MENTION_KEY + commentReplyDTO.getToUid())) {
                        mqSender.SendMentionMessage(commentReplyDTO.getToUid());
                        logger.info("Sent mention message for toReplyUid to MQ");
                    } else {
                        //
                    }
                } else {
                    logger.info("FromUid mention setting is off");
                }
            }
        } else {
            logger.info("Not reply on reply");
        }
    }
}


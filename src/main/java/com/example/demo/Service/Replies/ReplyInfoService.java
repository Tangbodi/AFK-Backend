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

    public void CalculateReplyTotalLike(List<ObjectUserDTO> objectUserDTOList) {
        logger.info("Finding all users like replies list with like status = 1");
        for (ObjectUserDTO objectUserDTO : objectUserDTOList) {
            Long replyId = objectUserDTO.getObjectId();
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
        boolean sameUser = false;
        boolean mentionOn = false;
        if (commentReplyDTO.getToReplyId() != null) {
            logger.info("This is a reply on reply");
            sameUser = commentReplyDTO.getFromUid().equals(commentReplyDTO.getToUid());
            mentionOn = replyOnCommentMentionService.CheckReplyOnCommentMention(commentReplyDTO, commentReplyDTO.getToUid());
            if (!sameUser && mentionOn) {
                messageService.SaveMessage(commentReplyDTO, commentReplyDTO.getToUid());
                if (redisService.CacheExists(MESSAGE_MENTION_KEY + commentReplyDTO.getToUid())) {
                    mqSender.SendMentionMessage(commentReplyDTO.getToUid());
                    logger.info("Sent reply mention message for toUid to MQ");
                } else {
                    //
                }
            } else {
                logger.info("FromUid is equal to ToUid or mention setting for toUid is off");
            }
        } else {
            //
        }
        sameUser = commentReplyDTO.getFromUid().equals(commentAuthorId);
        mentionOn = replyOnCommentMentionService.CheckReplyOnCommentMention(commentReplyDTO, commentAuthorId);
        if (!sameUser && mentionOn) {
            messageService.SaveMessage(commentReplyDTO, commentAuthorId);
            if (redisService.CacheExists(MESSAGE_MENTION_KEY + commentAuthorId)) {
                mqSender.SendMentionMessage(commentAuthorId);
                logger.info("Sent reply mention message for commentAuthorId to MQ");
            } else {
                //
            }
        } else {
            logger.info("FromUid is equal to ToUid or mention setting for commentAuthorId is off");
        }
    }
}


package com.example.demo.Service.Replies;

import com.example.demo.Mapper.Repository.PostUserMapRepository;
import com.example.demo.Model.DTO.CommentReplyDTO;
import com.example.demo.Model.DTO.ObjectUserDTO;
import com.example.demo.Model.Entity.RepliesInfo;
import com.example.demo.Model.Entity.UsersLikeReply;
import com.example.demo.Mapper.Repository.ReplyInfoRepository;
import com.example.demo.Mapper.Repository.UserLikeReplyRepository;
import com.example.demo.Service.Comments.CommentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.jms.JMSException;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

@Service
public class ReplyInfoService {
    private static final Logger logger = LoggerFactory.getLogger(ReplyInfoService.class);
    @Autowired
    private ReplyInfoRepository replyInfoRepository;
    @Autowired
    private UserLikeReplyRepository userLikeReplyRepository;
    @Autowired
    private PostUserMapRepository postUserMapRepository;
    @Autowired
    private CommentService commentService;
    @Autowired
    private ReplyOnCommentMentionService replyOnCommentMentionService;
    @Autowired
    private ReplyOnReplyMentionService replyOnReplyMentionService;
    @Autowired
    private ReplyOnPostMentionService replyOnPostMentionService;

    public void CalculateReplyTotalLike(List<ObjectUserDTO> objectUserDTOList){
        logger.info("Finding all users like replies list with like status = 1");
        for(ObjectUserDTO objectUserDTO : objectUserDTOList){
            Long replyId = objectUserDTO.getObjectId();
            Map<String,Object> map = userLikeReplyRepository.findReplyTotalLikeByLikeStatus(replyId);
            Integer totalLike= ((BigInteger) map.get("total_like")).intValue();
            UpdateReplyLikeCount(replyId, totalLike);
        }
    }

    private void UpdateReplyLikeCount(Long replyId, Integer totalLike){
        logger.info("Updating reply like count");
        RepliesInfo repliesInfo = replyInfoRepository.findById(replyId)
                .orElseGet(() -> CreateReplyInfo(replyId));
        repliesInfo.setLike(totalLike);
        replyInfoRepository.save(repliesInfo);
        logger.info("Updated reply like count");
    }
    private static RepliesInfo CreateReplyInfo(Long replyId){
        logger.info("Creating reply info");
        RepliesInfo repliesInfo = new RepliesInfo();
        repliesInfo.setId(replyId);
        repliesInfo.setLike(0);
        logger.info("Created reply info for reply ID: {}", replyId);
        return repliesInfo;
    }

    @Async("MultiExecutor")
    public void SetReplyMention(CommentReplyDTO commentReplyDTO) throws JMSException {
        Long toReplyAuthorId = commentReplyDTO.getToUid();
        Long commentAuthorId = commentService.GetCommentAuthorByCommentId(commentReplyDTO.getCommentId());
        Long postAuthorId = postUserMapRepository.findByPostId(commentReplyDTO.getPostId()).get().getId().getUserId();
        if (commentReplyDTO.getToReplyId() != null) {
            logger.info("This is a reply on reply");
            if (!commentReplyDTO.getFromUid().equals(toReplyAuthorId)) {
                logger.info("fromUid is not equal to the toUid");
                replyOnReplyMentionService.CheckReplyOnReplyMention(commentReplyDTO, toReplyAuthorId);
            }
        } else {
            logger.info("This is a reply on comment");
        }
        if (!commentReplyDTO.getFromUid().equals(commentAuthorId)) {
            logger.info("fromUid is not equal to the commentAuthorId");
            replyOnCommentMentionService.CheckReplyOnCommentMention(commentReplyDTO, commentAuthorId);
        }
        if (!commentReplyDTO.getFromUid().equals(postAuthorId)) {
            logger.info("fromUid is not equal to the postAuthorId");
            replyOnPostMentionService.CheckReplyOnPostMention(commentReplyDTO, postAuthorId);
        }
    }
}

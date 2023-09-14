package com.example.demo.Service.Replies;

import com.example.demo.Mapper.Repository.PostUserMapRepository;
import com.example.demo.Mapper.Repository.ReplyRepository;
import com.example.demo.Model.DTO.CommentReplyDTO;
import com.example.demo.Model.Entity.PostReply;
import com.example.demo.Model.VO.ReplySavedVO;
import com.example.demo.Service.Comments.CommentService;
import com.example.demo.Service.IP.IpAddressService;
import com.example.demo.Service.MQ.MQSender;
import com.example.demo.Service.Message.MessageService;
import com.example.demo.Service.Posts.PostInfoService;
import com.example.demo.Util.Snowflake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Service
public class ReplyService {
    private static final Logger logger = LoggerFactory.getLogger(ReplyService.class);
    @Autowired
    private ReplyRepository replyRepository;
    @Autowired
    private IpAddressService ipAddressService;
    @Autowired
    private PostInfoService postInfoService;
    @Autowired
    private MessageService messageService;
    @Autowired
    private MQSender mqSender;
    @Autowired
    private ReplyOnCommentMentionService replyOnCommentMentionService;
    @Autowired
    private ReplyOnReplyMentionService replyOnReplyMentionService;
    @Autowired
    private ReplyOnPostMentionService replyOnPostMentionService;
    @Lazy
    @Autowired
    private CommentService commentService;
    @Autowired
    private PostUserMapRepository postUserMapRepository;

    public ReplySavedVO SetReply(CommentReplyDTO commentReplyDTO) {
        logger.info("Setting reply");
        try {
            long replyId = Snowflake.generateUniqueId();
            commentReplyDTO.setReplyId(replyId);
            commentReplyDTO.setCreatedAt(Instant.now());
            PostReply postReply = new PostReply();
            postReply.setId(replyId);
            postReply.setCommentId(commentReplyDTO.getCommentId());
            postReply.setToReplyId(commentReplyDTO.getToReplyId());
            postReply.setContent(commentReplyDTO.getContent());
            postReply.setFromUid(commentReplyDTO.getFromUid());
            postReply.setToUid(commentReplyDTO.getToUid());
            postReply.setCreatedAt(commentReplyDTO.getCreatedAt());
            postReply.setModifiedAt(commentReplyDTO.getCreatedAt());
            PostReply savedReply = replyRepository.save(postReply);
            if (savedReply != null) {
                logger.info("Reply saved successfully");
                //set comment reply ip address
                ipAddressService.SetReplyIpAddress(commentReplyDTO);
                //send message to ActiveMQ
                mqSender.SendReplyCountMessage(commentReplyDTO);
                Long toReplyAuthorId = commentReplyDTO.getToUid();
                Long commentAuthorId = commentService.GetCommentAuthorByCommentId(commentReplyDTO.getCommentId());
                Long postAuthorId = postUserMapRepository.findByPostId(commentReplyDTO.getPostId()).get().getId().getUserId();
                if(commentReplyDTO.getToReplyId() != null){
                    logger.info("This is a reply on reply");
                    if(!commentReplyDTO.getFromUid().equals(toReplyAuthorId)){
                        logger.info("fromUid is not equal to the toUid");
                        replyOnReplyMentionService.CheckReplyOnReplyMention(commentReplyDTO, toReplyAuthorId);
                        if(!commentReplyDTO.getFromUid().equals(commentAuthorId)){
                            logger.info("fromUid is not equal to the commentAuthorId");
                            replyOnCommentMentionService.CheckReplyOnCommentMention(commentReplyDTO, commentAuthorId);
                            if(!commentReplyDTO.getFromUid().equals(postAuthorId)){
                                logger.info("fromUid is not equal to the postAuthorId");
                                replyOnPostMentionService.CheckReplyOnPostMention(commentReplyDTO, postAuthorId);
                            }
                        }
                    }
                } else {
                    logger.info("This is a reply on comment");
                    if(!commentReplyDTO.getFromUid().equals(commentAuthorId)){
                        logger.info("fromUid is not equal to the commentAuthorId");
                        replyOnCommentMentionService.CheckReplyOnCommentMention(commentReplyDTO, commentAuthorId);
                        if(!commentReplyDTO.getFromUid().equals(postAuthorId)){
                            logger.info("fromUid is not equal to the postAuthorId");
                            replyOnPostMentionService.CheckReplyOnPostMention(commentReplyDTO, postAuthorId);
                        }
                    }
                }
                return TransferToVO(commentReplyDTO);
            } else {
                logger.info("Failed to save reply: {}");
            }
        } catch (Exception e) {
            logger.error("Failed to set reply: {}", e);
        }
        return null;
    }

    public List<Map<String, Object>> GetRepliesByCommentId(List<Long> commentIds, Long userId) {
        logger.info("Getting replies by comment id");
        List<Map<String, Object>> replyList = replyRepository.findByCommentId(commentIds, userId);
        return replyList;
    }

    private static ReplySavedVO TransferToVO(CommentReplyDTO commentReplyDTO) {
        logger.info("Transferring reply to VO");
        ReplySavedVO replySavedVO = new ReplySavedVO();
        replySavedVO.setReplyId(commentReplyDTO.getReplyId().toString());
        replySavedVO.setCommentId(commentReplyDTO.getCommentId().toString());
        if (commentReplyDTO.getToReplyId() == null) {
            replySavedVO.setToReplyId("0");
        } else {
            replySavedVO.setToReplyId(commentReplyDTO.getToReplyId().toString());
        }
        replySavedVO.setToUid(commentReplyDTO.getToUid().toString());
        replySavedVO.setCreatedAt(commentReplyDTO.getCreatedAt());
        return replySavedVO;
    }
}


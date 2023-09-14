package com.example.demo.Service.Replies;

import com.example.demo.Mapper.Repository.ReplyOnCommentMentionRepository;
import com.example.demo.Model.DTO.CommentReplyDTO;
import com.example.demo.Model.DTO.UserRegisterDTO;
import com.example.demo.Model.Entity.ReplyOnCommentMention;
import com.example.demo.Model.VO.MessageVO;
import com.example.demo.Service.MQ.MQSender;
import com.example.demo.Service.Message.MessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.jms.JMSException;
import javax.transaction.Transactional;

@Service
public class ReplyOnCommentMentionService {
    private static final Logger logger = LoggerFactory.getLogger(ReplyOnCommentMentionService.class);
    @Autowired
    private ReplyOnCommentMentionRepository replyOnCommentMentionRepository;
    @Autowired
    private MessageService messageService;
    @Autowired
    private MQSender mqSender;

    @Transactional
    public void SaveReplyOnCommentMention(UserRegisterDTO userRegisterDTO) {
        logger.info("Saving ReplyOnCommentMention:{}");
        try {
            ReplyOnCommentMention replyOnCommentMention = new ReplyOnCommentMention();
            replyOnCommentMention.setId(userRegisterDTO.getUserId());
            replyOnCommentMention.setMentionOn(true);
            replyOnCommentMention.setCreatedAt(userRegisterDTO.getCreatedAt());
            replyOnCommentMention.setModifiedAt(userRegisterDTO.getCreatedAt());
            replyOnCommentMentionRepository.save(replyOnCommentMention);
            logger.info("Saved reply on comment mention setting");
        } catch (Exception e) {
            logger.error("Failed to save reply on comment mention setting: {}", e.getMessage(), e);
        }
    }

    public void CheckReplyOnCommentMention(CommentReplyDTO commentReplyDTO, Long commentAuthorId) throws JMSException {
        logger.info("Checking reply on comment mention setting for user:{}", commentAuthorId);
        try {
            ReplyOnCommentMention replyOnCommentMention = replyOnCommentMentionRepository.findById(commentAuthorId).orElse(null);
            if (replyOnCommentMention == null) {
                logger.info("User not found");
            } else {
                logger.info("User found: {}", replyOnCommentMention.getId());
                if (replyOnCommentMention.getMentionOn() == false) {
                    logger.info("Reply on comment mention setting is off");
                    return;
                } else {
                    logger.info("Reply on comment mention setting is on");
                    //set message mention
                    MessageVO messageVO = new MessageVO();
                    messageVO.setCommentReplyId(commentReplyDTO.getReplyId().toString());
                    messageVO.setContent(commentReplyDTO.getContent());
                    messageVO.setFromUid(commentReplyDTO.getFromUid().toString());
                    messageVO.setFromUsername(commentReplyDTO.getFromUsername());
                    messageVO.setToUid(commentAuthorId.toString());
                    messageVO.setCreatedAt(commentReplyDTO.getCreatedAt().toString());
                    //set message
                    messageService.SaveMessage(commentReplyDTO,commentAuthorId);
                    //send message mention to MQ
                    mqSender.SendMentionMessage(messageVO);
                    logger.info("Sent reply on comment mention message to MQ");
                }
            }
        } catch (Exception e) {
            logger.error("Failed to check reply on comment mention setting: {}", e.getMessage(), e);

        }
    }
}

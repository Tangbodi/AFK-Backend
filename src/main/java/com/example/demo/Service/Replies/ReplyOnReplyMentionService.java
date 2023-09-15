package com.example.demo.Service.Replies;

import com.example.demo.Mapper.Repository.ReplyOnReplyMentionRepository;
import com.example.demo.Model.DTO.CommentReplyDTO;
import com.example.demo.Model.DTO.UserRegisterDTO;
import com.example.demo.Model.Entity.ReplyOnReplyMention;
import com.example.demo.Model.VO.MessageVO;
import com.example.demo.Service.MQ.MQSender;
import com.example.demo.Service.Message.MessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class ReplyOnReplyMentionService {
    private static final Logger logger = LoggerFactory.getLogger(ReplyOnReplyMentionService.class);
    @Autowired
    private ReplyOnReplyMentionRepository replyOnReplyMentionRepository;
    @Autowired
    private MessageService messageService;
    @Autowired
    private MQSender mqSender;

    @Transactional
    public void SaveReplyOnReplyMention(UserRegisterDTO userRegisterDTO) {
        logger.info("Saving ReplyOnReplyMention:{}");
        try {
            ReplyOnReplyMention replyOnReplyMention = new ReplyOnReplyMention();
            replyOnReplyMention.setId(userRegisterDTO.getUserId());
            replyOnReplyMention.setMentionOn(true);
            replyOnReplyMention.setCreatedAt(userRegisterDTO.getCreatedAt());
            replyOnReplyMention.setModifiedAt(userRegisterDTO.getCreatedAt());
            replyOnReplyMentionRepository.save(replyOnReplyMention);
            logger.info("Saved reply on reply mention setting");
        } catch (Exception e) {
            logger.error("Failed to save reply on reply mention setting: {}", e.getMessage(), e);
        }
    }

    public void CheckReplyOnReplyMention(CommentReplyDTO commentReplyDTO, Long toReplyAuthorId) {
        logger.info("Checking reply on reply mention setting for user:{}", toReplyAuthorId);
        try {
            ReplyOnReplyMention replyOnReplyMention = replyOnReplyMentionRepository.findById(toReplyAuthorId).orElse(null);
            if (replyOnReplyMention == null) {
                logger.info("User not found");
            } else {
                logger.info("User found: {}", replyOnReplyMention.getId());
                if (replyOnReplyMention.getMentionOn() == false) {
                    logger.info("Reply on reply mention setting is off");
                } else {
                    logger.info("Reply on reply mention setting is on");
                    //set message mention
                    MessageVO messageVO = new MessageVO();
                    messageVO.setCommentReplyId(commentReplyDTO.getReplyId().toString());
                    messageVO.setContent(commentReplyDTO.getContent());
                    messageVO.setFromUid(commentReplyDTO.getFromUid().toString());
                    messageVO.setFromUsername(commentReplyDTO.getFromUsername());
                    messageVO.setToUid(toReplyAuthorId.toString());
                    messageVO.setCreatedAt(commentReplyDTO.getCreatedAt().toString());
                    //set message
                    messageService.SaveMessage(commentReplyDTO, toReplyAuthorId);
                    //send message mention to MQ
                    mqSender.SendMentionMessage(messageVO);
                    logger.info("Sent reply on reply mention message to MQ");
                }
            }
        } catch (Exception e) {
            logger.error("Failed to check reply on reply mention setting: {}", e.getMessage(), e);
        }
    }
}

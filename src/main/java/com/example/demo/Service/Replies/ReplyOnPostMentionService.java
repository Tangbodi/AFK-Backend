package com.example.demo.Service.Replies;

import com.example.demo.Mapper.Repository.ReplyOnPostMentionRepository;
import com.example.demo.Model.DTO.CommentReplyDTO;
import com.example.demo.Model.DTO.UserRegisterDTO;
import com.example.demo.Model.Entity.ReplyOnPostMention;
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
public class ReplyOnPostMentionService {
    private static final Logger logger = LoggerFactory.getLogger(ReplyOnPostMentionService.class);

    @Autowired
    private ReplyOnPostMentionRepository replyOnPostMentionRepository;
    @Autowired
    private MessageService messageService;
    @Autowired
    private MQSender mqSender;

    @Transactional
    public void SaveReplyOnPostMention(UserRegisterDTO userRegisterDTO){
        logger.info("Saving ReplyOnPostMention:{}");
        try{
            ReplyOnPostMention replyOnPostMention = new ReplyOnPostMention();
            replyOnPostMention.setId(userRegisterDTO.getUserId());
            replyOnPostMention.setMentionOn(true);
            replyOnPostMention.setCreatedAt(userRegisterDTO.getCreatedAt());
            replyOnPostMention.setModifiedAt(userRegisterDTO.getCreatedAt());
            replyOnPostMentionRepository.save(replyOnPostMention);
            logger.info("Saved reply on post mention setting");
        }catch (Exception e){
            logger.error("Failed to save reply on post mention setting: {}",e.getMessage(),e);
        }
    }
    public void CheckReplyOnPostMention(CommentReplyDTO commentReplyDTO, Long postAuthorId){
        logger.info("Checking reply on post mention setting for user:{}",postAuthorId);
        try{
            ReplyOnPostMention replyOnPostMention = replyOnPostMentionRepository.findById(postAuthorId).orElse(null);
            if(replyOnPostMention == null){
                logger.info("User not found");
            } else {
                logger.info("User found: {}",replyOnPostMention.getId());
                if(replyOnPostMention.getMentionOn() == false){
                    logger.info("Reply on post mention setting is off");
                } else {
                    logger.info("Reply on post mention setting is on");
                    //set message mention
                    MessageVO messageVO = new MessageVO();
                    messageVO.setCommentReplyId(commentReplyDTO.getReplyId().toString());
                    messageVO.setContent(commentReplyDTO.getContent());
                    messageVO.setFromUid(commentReplyDTO.getFromUid().toString());
                    messageVO.setFromUsername(commentReplyDTO.getFromUsername());
                    messageVO.setToUid(postAuthorId.toString());
                    messageVO.setCreatedAt(commentReplyDTO.getCreatedAt().toString());
                    //set message
                    messageService.SaveMessage(commentReplyDTO,postAuthorId);
                    //send message mention to MQ
                    mqSender.SendMentionMessage(messageVO);
                    logger.info("Sent reply on reply mention message to MQ");
                }
            }
        }catch (Exception e){
            logger.error("Failed to check reply on reply mention setting: {}",e.getMessage(),e);
        }
    }
}

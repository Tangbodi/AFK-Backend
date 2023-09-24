package com.example.demo.Service.Replies;

import com.example.demo.Mapper.Repository.ReplyOnReplyMentionRepository;
import com.example.demo.Model.DTO.CommentReplyDTO;
import com.example.demo.Model.DTO.UserRegisterDTO;
import com.example.demo.Model.Entity.ReplyOnReplyMention;
import com.example.demo.Model.VO.MessageVO;
import com.example.demo.Service.MQ.MQSender;
import com.example.demo.Service.Message.MessageService;
import com.example.demo.Service.Redis.RedisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

//@Service
//public class ReplyOnReplyMentionService {
//    private static final Logger logger = LoggerFactory.getLogger(ReplyOnReplyMentionService.class);
//    @Autowired
//    private ReplyOnReplyMentionRepository replyOnReplyMentionRepository;
//
//
//    @Async("MultiExecutor")
//    @Transactional
//    public void SaveReplyOnReplyMention(UserRegisterDTO userRegisterDTO) {
//        logger.info("Saving ReplyOnReplyMention:{}");
//        try {
//            ReplyOnReplyMention replyOnReplyMention = new ReplyOnReplyMention();
//            replyOnReplyMention.setId(userRegisterDTO.getUserId());
//            replyOnReplyMention.setMentionOn(true);
//            replyOnReplyMention.setCreatedAt(userRegisterDTO.getCreatedAt());
//            replyOnReplyMention.setModifiedAt(userRegisterDTO.getCreatedAt());
//            replyOnReplyMentionRepository.save(replyOnReplyMention);
//            logger.info("Saved reply on reply mention setting");
//        } catch (Exception e) {
//            logger.error("Failed to save reply on reply mention setting: {}", e.getMessage(), e);
//        }
//    }
//
//    public boolean CheckReplyOnReplyMention(CommentReplyDTO commentReplyDTO, Long toReplyAuthorId) {
//        logger.info("Checking reply on reply mention setting for user:{}", toReplyAuthorId);
//        try {
//            ReplyOnReplyMention replyOnReplyMention = replyOnReplyMentionRepository.findById(toReplyAuthorId).orElse(null);
//            if (replyOnReplyMention == null) {
//                logger.info("User not found");
//            } else {
//                logger.info("User found: {}", replyOnReplyMention.getId());
//                if (replyOnReplyMention.getMentionOn() == false) {
//                    logger.info("Reply on reply mention setting is off");
//                    return false;
//                } else {
//                    logger.info("Reply on reply mention setting is on");
//                    return true;
//                }
//            }
//        } catch (Exception e) {
//            logger.error("Failed to check reply on reply mention setting: {}", e.getMessage(), e);
//        }
//        return false;
//    }
//}

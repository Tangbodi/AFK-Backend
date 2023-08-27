package com.example.demo.Service.Message;

import com.example.demo.Model.Entity.Message;
import com.example.demo.Repository.MessageRepository;
import com.example.demo.Repository.MessageUserMapRepository;
import com.example.demo.Model.DTO.CommentReplyDTO;
import com.example.demo.Model.Entity.MessagesUsersMap;
import com.example.demo.Model.VO.MessageVO;
import com.example.demo.Service.Redis.RedisMessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class MessageService {
    private static final Logger logger = LoggerFactory.getLogger(MessageService.class);
    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private MessageUserMapRepository messageUserMapRepository;
    @Autowired
    private RedisMessageService redisMessageService;

    @Transactional
    public Message SetMessage(CommentReplyDTO commentReplyDTO) {
        logger.info("Setting message");
        Message message = new Message();
        try {
            if(commentReplyDTO.getReplyId() != null) {
                message.setCommentReplyId(commentReplyDTO.getReplyId());
            } else {
                message.setCommentReplyId(commentReplyDTO.getCommentId());
            }
            message.setContent(commentReplyDTO.getContent());
            message.setFromUid(commentReplyDTO.getFromUid());
            message.setToUid(commentReplyDTO.getToUid());
            message.setCreatedAt(commentReplyDTO.getCreatedAt());
            message.setModifiedAt(commentReplyDTO.getCreatedAt());
            Message savedMessage = messageRepository.save(message);
            SetMessageUserMap(savedMessage);
        } catch (Exception e) {
            logger.error("Failed to set reply mention", e.getMessage(),e);
        }
        return null;
    }

    @Transactional
    public void SetMessageUserMap(Message savedMessage) {
        logger.info("Setting message user map");
        try {
            MessagesUsersMap messagesUsersMap = new MessagesUsersMap();
            messagesUsersMap.setMessageId(savedMessage.getId());
            messagesUsersMap.setMentionedUid(savedMessage.getToUid());
            messagesUsersMap.setReadStatus(false);
            messageUserMapRepository.save(messagesUsersMap);
            redisMessageService.SetUserReadStatus(savedMessage.getToUid());
        } catch (Exception e) {
            logger.error("Failed to set message user map", e.getMessage(),e);
        }
    }

    public List<MessageVO> GetUnreadMessageViaMessageUserMap(Long userId) {
        logger.info("Getting unread message");
        List<Map<Short, Object>> messagesUsersMapList;
        try {
            messagesUsersMapList = messageUserMapRepository.findUnreadMessages(userId);
            logger.info("Unread message list size: " + messagesUsersMapList.size());
            if (!messagesUsersMapList.isEmpty()) {
                redisMessageService.SetUserReadStatus(userId);
                List<MessageVO> messageVOList = new ArrayList<>();
                for (Map<Short, Object> messagesUsersMap : messagesUsersMapList) {
                    MessageVO messageVO = new MessageVO();
                    messageVO.setCrId(((BigInteger) messagesUsersMap.get("reply_id")).longValue());
                    messageVO.setFromUid(((BigInteger) messagesUsersMap.get("user_id")).longValue());
                    messageVO.setFromUsername((String) messagesUsersMap.get("username"));
                    messageVO.setContent((String) messagesUsersMap.get("content"));
                    messageVOList.add(messageVO);
                }
                return messageVOList;
            }
        } catch (Exception e) {
            logger.error("Failed to get unread message", e.getMessage(),e);
        }
        return Collections.emptyList();
    }

    @Transactional
    public void UpdateMessageUserMap(Long userId) {
        logger.info("Updating read status");
        List<MessagesUsersMap> messagesUsersMapList;
        try {
            messagesUsersMapList = messageUserMapRepository.findUnreadMessagesByMentionedUid(userId);
            if (!messagesUsersMapList.isEmpty()) {
                messagesUsersMapList.stream()
                        .forEach(messagesUsersMap -> {
                            messagesUsersMap.setReadStatus(true);
                            messageUserMapRepository.save(messagesUsersMap);
                        });
            }
            redisMessageService.DeleteUserReadStatus(userId);
        } catch (Exception e) {
            logger.error("Failed to update read status", e.getMessage(),e);
            throw new RuntimeException("Failed to update read status " + e);
        }
    }
}

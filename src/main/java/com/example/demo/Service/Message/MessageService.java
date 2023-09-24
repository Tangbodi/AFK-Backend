package com.example.demo.Service.Message;

import com.example.demo.Mapper.Repository.MessageRepository;
import com.example.demo.Mapper.Repository.MessageUserMapRepository;
import com.example.demo.Model.DTO.CommentReplyDTO;
import com.example.demo.Model.DTO.MessageDTO;
import com.example.demo.Model.Entity.Message;
import com.example.demo.Model.Entity.MessagesUsersMap;
import com.example.demo.Model.VO.MessageVO;
import com.example.demo.Service.Redis.RedisMessageService;
import com.example.demo.Service.Redis.RedisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class MessageService {
    private static final Logger logger = LoggerFactory.getLogger(MessageService.class);
    private static final String MESSAGE_MENTION_KEY = "UNREAD:";
    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private MessageUserMapRepository messageUserMapRepository;
    @Autowired
    private RedisMessageService redisMessageService;
    @Autowired
    private RedisService redisService;

    @Transactional
    public Message SaveMessage(CommentReplyDTO commentReplyDTO, Long toUid) {
        logger.info("Saving message");
        Message message = new Message();
        try {
            if (commentReplyDTO.getReplyId() != null) {
                message.setCommentReplyId(commentReplyDTO.getReplyId());
            } else {
                message.setCommentReplyId(commentReplyDTO.getCommentId());
            }
            //set message
            message.setContent(commentReplyDTO.getContent().substring(0, 37));
            message.setFromUid(commentReplyDTO.getFromUid());
            message.setToUid(toUid);
            message.setCreatedAt(commentReplyDTO.getCreatedAt());
            message.setModifiedAt(commentReplyDTO.getCreatedAt());
            Message savedMessage = messageRepository.save(message);
            logger.info("Saved message");
            //set message user map
            SaveMessageUserMap(savedMessage);
        } catch (Exception e) {
            logger.error("Failed to set reply mention", e.getMessage(), e);
        }
        return null;
    }

    @Transactional
    public Message SaveMessage(MessageDTO messageDTO) {
        logger.info("Saving message");
        Message message = new Message();
        try {
            message.setCommentReplyId(Long.valueOf(messageDTO.getCommentReplyId()));
            //set message
            int contentLength = messageDTO.getContent().length();
            message.setContent(messageDTO.getContent().substring(0, contentLength/2));
            message.setFromUid(Long.valueOf(messageDTO.getFromUid()));
            message.setToUid(Long.valueOf(messageDTO.getToUid()));
            message.setCreatedAt(messageDTO.getCreatedAt());
            message.setModifiedAt(messageDTO.getCreatedAt());
            message.setTypeId(messageDTO.getTypeId().byteValue());
            Message savedMessage = messageRepository.save(message);
            logger.info("Saved message");
            //set message user map
            SaveMessageUserMap(savedMessage);
            if(redisService.CacheExists(MESSAGE_MENTION_KEY+savedMessage.getToUid())){
                redisMessageService.GetUnreadMessageByUserId(savedMessage.getToUid());
            } else {
                //
            }
        } catch (Exception e) {
            logger.error("Failed to set reply mention", e.getMessage(), e);
        }
        return null;
    }

    @Transactional
    public void SaveMessageUserMap(Message savedMessage) {
        logger.info("Saving message user map");
        try {
            MessagesUsersMap messagesUsersMap = new MessagesUsersMap();
            messagesUsersMap.setMessageId(savedMessage.getId());
            messagesUsersMap.setMentionedUid(savedMessage.getToUid());
            messagesUsersMap.setReadStatus(false);
            messageUserMapRepository.save(messagesUsersMap);
            logger.info("Saved message user map");
        } catch (Exception e) {
            logger.error("Failed to set message user map", e.getMessage(), e);
        }
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
            } else {
                //
            }
            redisMessageService.DeleteUnreadMessage(userId);
        } catch (Exception e) {
            logger.error("Failed to update read status", e.getMessage(), e);
            throw new RuntimeException("Failed to update read status " + e);
        }
    }

    public List<MessageVO> GetMessageHistoryByUserId(Long userId) {
        logger.info("Getting messages by user id");
        try {
            List<Map<Short, Object>> messagesList = messageRepository.getMessageHistoryByUserId(userId);
            if (!messagesList.isEmpty()) {
                logger.info("Found messages by user id: {}" + userId);
                return TransferToNotificationVO(messagesList);
            } else {
                logger.info("No messages found by user id: {}", userId);
                return Collections.emptyList();
            }
        } catch (Exception e) {
            logger.error("Failed to get messages by user id", e.getMessage(), e);
        }
        return Collections.emptyList();
    }

    private static List<MessageVO> TransferToNotificationVO(List<Map<Short, Object>> messagesList) {
        logger.info("Transferring messages to VO");
        List<MessageVO> messageVOList = new ArrayList<>();
        for (Map<Short, Object> map : messagesList) {
            MessageVO messageVO = new MessageVO();
            messageVO.setMessageId(map.get("message_id").toString());
            messageVO.setObjectId(map.get("object_id").toString());
            messageVO.setFromUid(map.get("from_uid").toString());
            messageVO.setToUid(map.get("to_uid").toString());
            messageVO.setFromUsername(map.get("from_username").toString());
            messageVO.setFromAvatarUrl(map.get("from_avatar_url").toString());
            messageVO.setContent(map.get("content").toString());
            messageVO.setTypeId((map.get("type_id").toString()));
            Timestamp timestamp = (Timestamp) map.get("created_at");
            messageVO.setCreatedAt(timestamp.toInstant().toString());
            messageVOList.add(messageVO);
        }
        return messageVOList;
    }
}

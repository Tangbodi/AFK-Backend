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
import org.springframework.scheduling.annotation.Async;
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
    private static final int MENTIONED_MESSAGE_LENGTH = 33;
    private static final String MENTIONED_MESSAGE_SUFFIX = "...";
    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private MessageUserMapRepository messageUserMapRepository;
    @Autowired
    private RedisMessageService redisMessageService;
    @Autowired
    private RedisService redisService;

    @Async("MultiExecutor")
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
            int maxLength = commentReplyDTO.getContent().length();
            String content;
            if (maxLength > MENTIONED_MESSAGE_LENGTH) {
                content = commentReplyDTO.getContent().substring(0, Math.min(maxLength, MENTIONED_MESSAGE_LENGTH)) + MENTIONED_MESSAGE_SUFFIX;
            } else {
                content = commentReplyDTO.getContent();
            }
            message.setPostId(commentReplyDTO.getPostId());
            message.setContent(content);
            message.setFromUid(commentReplyDTO.getFromUid());
            message.setToUid(toUid);
            message.setCreatedAt(commentReplyDTO.getCreatedAt());
            message.setModifiedAt(commentReplyDTO.getCreatedAt());
            message.setTypeId(commentReplyDTO.getTypeId().byteValue());
            Message savedMessage = messageRepository.save(message);
            logger.info("Saved message");
            //set message user map
            SaveMessageUserMap(savedMessage);
        } catch (Exception e) {
            logger.error("Failed to set reply mention", e.getMessage(), e);
        }
        return null;
    }

    @Async("MultiExecutor")
    @Transactional
    public Message SaveMessage(MessageDTO messageDTO) {
        logger.info("Saving message");
        Message message = new Message();
        try {
            //set message
            int maxLength = messageDTO.getContent().length();
            String content;
            if (maxLength > MENTIONED_MESSAGE_LENGTH) {
                content = messageDTO.getContent().substring(0, Math.min(maxLength, MENTIONED_MESSAGE_LENGTH)) + MENTIONED_MESSAGE_SUFFIX;
            } else {
                content = messageDTO.getContent();
            }
            logger.info("post id: {}", messageDTO.getPostId());
            logger.info("comment reply id: {}", messageDTO.getCommentReplyId());
            message.setPostId(messageDTO.getPostId());
            message.setCommentReplyId(messageDTO.getCommentReplyId());
            message.setContent(content);
            message.setFromUid(messageDTO.getFromUid());
            message.setToUid(messageDTO.getToUid());
            message.setCreatedAt(messageDTO.getCreatedAt());
            message.setModifiedAt(messageDTO.getCreatedAt());
            message.setTypeId(messageDTO.getTypeId().byteValue());
            Message savedMessage = messageRepository.save(message);
            logger.info("Saved message");
            //set message user map
            SaveMessageUserMap(savedMessage);
            if (redisService.CacheExists(MESSAGE_MENTION_KEY + savedMessage.getToUid())) {
                GetUnreadMessageByUserId(savedMessage.getToUid());
            } else {
                //
            }
        } catch (Exception e) {
            logger.error("Failed to set reply mention", e.getMessage(), e);
        }
        return null;
    }

    public void GetUnreadMessageByUserId(Long userId) {
        logger.info("Setting unread messages by user id");
        try {
            List<Map<Short, Object>> messagesList = messageRepository.getUnreadMessagesByUserId(userId);
            redisMessageService.SetUnreadMessageToRedis(messagesList, userId);
        } catch (Exception e) {
            logger.error("Failed to get unread messages by user id", e.getMessage(), e);
        }
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
        logger.info("Updating UpdateMessageUserMap read status");
        try {
            List<MessageVO> unreadMessageVOList = redisMessageService.GetUnreadMessageFromRedis(userId);
            if (!unreadMessageVOList.isEmpty()) {
                List<Long> messageIds = new ArrayList<>();
                for (MessageVO messageVO : unreadMessageVOList) {
                    messageIds.add(Long.valueOf(messageVO.getMessageId()));
                }
                messageUserMapRepository.updateReadStatusByMessageId(messageIds);
                GetUnreadMessageByUserId(userId);
            } else {
                //
            }
        } catch (Exception e) {
            logger.error("Failed to update UpdateMessageUserMap read status", e.getMessage(), e);
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
            messageVO.setMessageId(String.valueOf(map.get("message_id")));
            messageVO.setCommentReplyId(String.valueOf(map.get("comment_reply_id")));
            messageVO.setFromUid(String.valueOf(map.get("from_uid")));
            messageVO.setToUid(String.valueOf(map.get("to_uid")));
            messageVO.setFromUsername(String.valueOf(map.get("from_username")));
            messageVO.setFromAvatarUrl(String.valueOf(map.get("from_avatar_url")));
            messageVO.setContent(String.valueOf(map.get("content")));
            messageVO.setTypeId((String.valueOf(map.get("type_id"))));
            Timestamp timestamp = (Timestamp) map.get("created_at");
            messageVO.setCreatedAt(String.valueOf(timestamp.toInstant()));
            messageVOList.add(messageVO);
        }
        return messageVOList;
    }
}

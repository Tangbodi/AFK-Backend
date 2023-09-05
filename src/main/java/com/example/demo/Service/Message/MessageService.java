package com.example.demo.Service.Message;

import com.example.demo.Model.Entity.Message;
import com.example.demo.Mapper.Repository.MessageRepository;
import com.example.demo.Mapper.Repository.MessageUserMapRepository;
import com.example.demo.Model.DTO.CommentReplyDTO;
import com.example.demo.Model.Entity.MessagesUsersMap;
import com.example.demo.Model.VO.MessageVO;
import com.example.demo.Service.MQ.MQSender;
import com.example.demo.Service.Redis.RedisMessageService;
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
        MessageVO messageVO = new MessageVO();
        try {
            if(commentReplyDTO.getReplyId() != null) {
                message.setCommentReplyId(commentReplyDTO.getReplyId());
                //set message mention id
                messageVO.setCommentReplyId(commentReplyDTO.getReplyId().toString());
            } else {
                message.setCommentReplyId(commentReplyDTO.getCommentId());
                //set message mention id
                messageVO.setCommentReplyId(commentReplyDTO.getCommentId().toString());
            }
            //set message
            message.setContent(commentReplyDTO.getContent());
            message.setFromUid(commentReplyDTO.getFromUid());
            message.setToUid(commentReplyDTO.getToUid());
            message.setCreatedAt(commentReplyDTO.getCreatedAt());
            message.setModifiedAt(commentReplyDTO.getCreatedAt());
            Message savedMessage = messageRepository.save(message);
            //set message user map
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
        } catch (Exception e) {
            logger.error("Failed to set message user map", e.getMessage(),e);
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
            logger.error("Failed to update read status", e.getMessage(),e);
            throw new RuntimeException("Failed to update read status " + e);
        }
    }
    public List<MessageVO> GetMessageHistoryByUserId(Long userId){
        logger.info("Getting messages by user id");
        try {
            List<Map<Short, Object>> messagesList = messageRepository.getMessageHistoryByUserId(userId);
            if(!messagesList.isEmpty()){
                logger.info("Found messages by user id: {}"+userId);
                return TransferToNotificationVO(messagesList);
            } else {
                logger.info("No messages found by user id: {}",userId);
                return Collections.emptyList();
            }
        } catch (Exception e) {
            logger.error("Failed to get messages by user id", e.getMessage(),e);
        }
        return Collections.emptyList();
    }
    private static List<MessageVO> TransferToNotificationVO(List<Map<Short, Object>> messagesList){
        logger.info("Transferring messages to VO");
        List<MessageVO> messageVOList = new ArrayList<>();
       for(Map<Short, Object> map : messagesList){
              MessageVO messageVO = new MessageVO();
              messageVO.setCommentReplyId(map.get("comment_reply_id").toString());
              messageVO.setContent((String) map.get("content"));
              messageVO.setFromUid(map.get("from_uid").toString());
              messageVO.setFromUsername((String) map.get("from_username"));
              messageVO.setToUid(map.get("to_uid").toString());
              Timestamp timestamp = (Timestamp) map.get("created_at");
              messageVO.setCreatedAt(timestamp.toInstant().toString());
              messageVOList.add(messageVO);
       }
        return messageVOList;
    }
}

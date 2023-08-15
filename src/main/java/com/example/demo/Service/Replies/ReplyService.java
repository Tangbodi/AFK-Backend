package com.example.demo.Service.Replies;

import com.example.demo.Mapper.Repository.MessagesRepository;
import com.example.demo.Mapper.Repository.MessagesUsersMapRepository;
import com.example.demo.Mapper.Repository.ReplyRepository;
import com.example.demo.Model.DTO.ReplyDTO;
import com.example.demo.Model.Entity.Message;
import com.example.demo.Model.Entity.MessagesUsersMap;
import com.example.demo.Model.Entity.PostReply;
import com.example.demo.Model.VO.MessageVO;
import com.example.demo.Model.VO.ReplyVO;
import com.example.demo.Service.Redis.RedisMessageService;
import com.example.demo.Util.UUIDCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigInteger;
import java.util.*;

@Service
public class ReplyService {
    private static final Logger logger = LoggerFactory.getLogger(ReplyService.class);
    @Autowired
    private ReplyRepository replyRepository;
    @Autowired
    private MessagesRepository messagesRepository;
    @Autowired
    private MessagesUsersMapRepository messagesUsersMapRepository;
    @Autowired
    private RedisMessageService redisMessageService;

    public ReplyVO SetReply(ReplyDTO replyDTO) {
        logger.info("Setting reply");
        try {
            String uuid = UUIDCreator.CreateUUID();
            replyDTO.setReplyId(uuid);
            PostReply postReply = new PostReply();
            postReply.setReplyId(uuid);
            postReply.setCommentId(replyDTO.getCommentId());
            postReply.setParentReplyId(replyDTO.getParentReplyId());
            postReply.setReplyType(replyDTO.getReplyType());
            postReply.setContent(replyDTO.getContent());
            postReply.setFromUid(replyDTO.getFromUid());
            postReply.setToUid(replyDTO.getToUid());
            postReply.setIpvFour(replyDTO.getIpvFour());
            postReply.setIpvSix(replyDTO.getIpvSix());
            postReply.setCreatedAt(replyDTO.getCreatedAt());
            postReply.setModifiedAt(replyDTO.getCreatedAt());
            PostReply savedReply = replyRepository.save(postReply);
            if (savedReply != null) {
                logger.info("Reply saved successfully");
                return TransferToVO(replyDTO);
            } else {
                logger.info("Failed to save reply");
            }
        } catch (Exception e) {
            logger.error("Failed to set reply", e);
        }
        return null;
    }

    private static ReplyVO TransferToVO(ReplyDTO replyDTO) {
        ReplyVO replyVO = new ReplyVO();
        replyVO.setReplyId(replyDTO.getReplyId());
        replyVO.setCommentId(replyDTO.getCommentId());
        replyVO.setParentReplyId(replyDTO.getParentReplyId());
        replyVO.setToUid(replyDTO.getToUid());
        replyVO.setCreatedAt(replyDTO.getCreatedAt());
        return replyVO;
    }

    public Message SetMessage(ReplyDTO replyDTO) {
        logger.info("Setting message");
        try {
            Message message = new Message();
            message.setReplyId(replyDTO.getReplyId());
            message.setContent(replyDTO.getContent());
            message.setFromUid(replyDTO.getFromUid());
            message.setToUid(replyDTO.getToUid());
            message.setCreatedAt(replyDTO.getCreatedAt());
            message.setModifiedAt(replyDTO.getCreatedAt());
            return messagesRepository.save(message);
        } catch (Exception e) {
            logger.error("Failed to set reply mention", e);
        }
        return null;
    }

    public void SetMessageUserMap(Message message) {
        logger.info("Setting message user map");
        try {
            MessagesUsersMap messagesUsersMap = new MessagesUsersMap();
            messagesUsersMap.setMessageId(message.getId());
            messagesUsersMap.setMentionedUid(message.getToUid());
            messagesUsersMap.setReadStatus(false);
            messagesUsersMapRepository.save(messagesUsersMap);
        } catch (Exception e) {
            logger.error("Failed to set message user map", e);
        }
    }

    public List<MessageVO> GetUnreadMessageViaMessageUserMap(String userId) {
        logger.info("Getting unread message");
        List<Map<Short, Object>> messagesUsersMapList;
        try {
            messagesUsersMapList = messagesUsersMapRepository.findUnreadMessages(userId);
            logger.info("Unread message list size: " + messagesUsersMapList.size());
            if (!messagesUsersMapList.isEmpty()) {
                redisMessageService.SetUserReadStatus(userId);
                List<MessageVO> messageVOList = new ArrayList<>();
                for (Map<Short, Object> messagesUsersMap : messagesUsersMapList) {
                    MessageVO messageVO = new MessageVO();
                    messageVO.setReplyId((String) messagesUsersMap.get("reply_id"));
                    messageVO.setFromUid((String) messagesUsersMap.get("username"));
                    messageVO.setContent((String) messagesUsersMap.get("content"));
                    messageVOList.add(messageVO);
                }
                return messageVOList;
            }
        } catch (Exception e) {
            logger.error("Failed to get unread message", e);
        }
        return Collections.emptyList();
    }
    @Transactional
    public void UpdateMessageUserMap(String userId){
        logger.info("Updating read status");
        List<MessagesUsersMap> messagesUsersMapList;
        try{
            messagesUsersMapList = messagesUsersMapRepository.findUnreadMessagesByMentionedUid(userId);
            if (!messagesUsersMapList.isEmpty()){
                messagesUsersMapList.stream()
                        .forEach(messagesUsersMap -> {
                            messagesUsersMap.setReadStatus(true);
                            messagesUsersMapRepository.save(messagesUsersMap);
                        });
            }
            redisMessageService.DeleteUserReadStatus(userId);
        } catch (Exception e){
            logger.error("Failed to update read status", e);
        }
    }
}


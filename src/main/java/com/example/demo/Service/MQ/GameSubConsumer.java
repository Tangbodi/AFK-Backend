package com.example.demo.Service.MQ;

import com.example.demo.Mapper.Repository.GameRepository;
import com.example.demo.Mapper.Repository.MessageRepository;
import com.example.demo.Mapper.Repository.UserFavoriteGameRepository;
import com.example.demo.Model.DTO.NewPostNotificationDTO;
import com.example.demo.Model.DTO.PostDTO;
import com.example.demo.Model.VO.MessageVO;
import com.example.demo.Service.Message.MessageService;
import com.example.demo.Service.Redis.RedisMessageService;
import com.example.demo.Service.Redis.RedisService;
import org.apache.activemq.command.ActiveMQObjectMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;

import javax.jms.JMSException;
import javax.jms.Message;
import java.util.List;
import java.util.Map;

public class GameSubConsumer {
    private static final Logger logger = LoggerFactory.getLogger(GameSubConsumer.class);
    private static final String MESSAGE_MENTION_KEY = "UNREAD:";
    private static final String TypeId = "7";
    @Autowired
    private UserFavoriteGameRepository userFavoriteGameRepository;
    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private RedisService redisService;
    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private RedisMessageService redisMessageService;
    @JmsListener(destination = "new-post-redis")
    public void NewPostNotification(Message message) throws JMSException {
        // Implement logic to send notifications to subscribed users.
        ActiveMQObjectMessage activeMqObjectMessage = (ActiveMQObjectMessage) message;
        NewPostNotificationDTO newPostNotificationDTO = (NewPostNotificationDTO) activeMqObjectMessage.getObject();
        try {
            logger.info("Sending new post notification");
            List<Map<Short, Object>> userSavedGameAndMentionOn = userFavoriteGameRepository.findUserSavedGameAndMentionOn(newPostNotificationDTO.getGameId());
            if (!userSavedGameAndMentionOn.isEmpty()) {
                String gameName = gameRepository.findById(newPostNotificationDTO.getGameId()).get().getGameName();
                for(Map<Short, Object> map : userSavedGameAndMentionOn){
                    Long userId = Long.valueOf(map.get("user_id").toString());
                    if (redisService.CacheExists(MESSAGE_MENTION_KEY + userId)) {
                        logger.info("User is online: {}");
                        List<MessageVO> unreadMessage = redisMessageService.GetUnreadMessageFromRedis(userId);
                        MessageVO messageVO = new MessageVO();
                        messageVO.setContent("New post in " + gameName+"!");
                        messageVO.setGenreId(newPostNotificationDTO.getGenreId().toString());
                        messageVO.setGameId(newPostNotificationDTO.getGameId().toString());
                        messageVO.setPostId(newPostNotificationDTO.getPostId().toString());
                        messageVO.setToUid(userId.toString());
                        messageVO.setTypeId(TypeId);
                        unreadMessage.add(messageVO);
                        redisMessageService.UpdateUnreadMessageFromRedis(unreadMessage);
                    } else {
                        logger.info("User is offline: {}");
                    }
                }
            } else {
                logger.info("No user subscribed to this game or mention on");
            }
        } catch (Exception e) {
            logger.error("Error occurred when sending new post notification: " + e.getMessage(), e);
        }
    }
}

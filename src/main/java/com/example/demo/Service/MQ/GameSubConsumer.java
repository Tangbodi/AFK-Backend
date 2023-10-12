package com.example.demo.Service.MQ;

import com.example.demo.Mapper.Repository.GameRepository;
import com.example.demo.Mapper.Repository.MessageRepository;
import com.example.demo.Mapper.Repository.UserFavoriteGameRepository;
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
    private static final String USER_SETTING = "USER_SETTING";
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
    @JmsListener(destination = "new-post-redis", containerFactory = "activeMQFactory")
    public void NewPostNotification(Message message) throws JMSException {
        // Implement logic to send notifications to subscribed users.
        ActiveMQObjectMessage activeMqObjectMessage = (ActiveMQObjectMessage) message;
        PostDTO postDTO = (PostDTO) activeMqObjectMessage.getObject();
        try {
            List<Map<Short, Object>> userSavedGameAndMentionOn = userFavoriteGameRepository.findUserSavedGameAndMentionOn(postDTO.getGameId());
            if (!userSavedGameAndMentionOn.isEmpty()) {
                String gameName = gameRepository.findById(postDTO.getGameId()).get().getGameName();
                for(Map<Short, Object> map : userSavedGameAndMentionOn){
                    Long userId = Long.valueOf(map.get("user_id").toString());
                    if (redisService.MemberExists(USER_SETTING, userId)) {
                        logger.info("User is online: {}");
                        List<MessageVO> unreadMessage = redisMessageService.GetUnreadMessageFromRedis(userId);
                        MessageVO messageVO = new MessageVO();
                        messageVO.setContent("New post in " + gameName+"!");
                        messageVO.setGenreId(postDTO.getGenreId().toString());
                        messageVO.setGameId(postDTO.getGameId().toString());
                        messageVO.setPostId(postDTO.getPostId().toString());
                        messageVO.setToUid(userId.toString());
                        messageVO.setTypeId(TypeId);
                        unreadMessage.add(messageVO);
                        redisMessageService.UpdateUnreadMessageFromRedis(unreadMessage);
                    } else {
                        logger.info("User is offline: {}");
                    }
                }
            } else {
                //
            }
        } catch (Exception e) {
            logger.error("Error occurred when sending new post notification: " + e.getMessage(), e);
        }
    }
}

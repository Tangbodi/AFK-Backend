package com.example.demo.Service.Redis;

import com.example.demo.Model.VO.GameIconVO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

import java.util.List;
import java.util.UUID;

@Service

public class RedisService {
    private static final Logger logger = LoggerFactory.getLogger(RedisService.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final String ALL_GAME_ICON_KEY = "ALL_GAME_ICON";

    private String GenerateEmailToken() {

        return UUID.randomUUID().toString();
    }

    public String SetUpdateEmailCache(String newEmail) {
        logger.info("Setting up redis cache for email update: {}" + newEmail);
        Jedis jedis = new Jedis("localhost");
        try {
            String token = GenerateEmailToken();
            jedis.mset(token, newEmail);
            jedis.expire(token, 600);
            logger.info("Redis cache for email update set up successfully: {}" + newEmail);
            return token;
        } catch (Exception e) {
            logger.error("Failed to set up redis cache for email update", e);
        } finally {
            logger.info("Closing the jedis connection:::");
            jedis.close();
        }
        return null;
    }

    public boolean CheckUpdateEmailCache(String token) {
        logger.info("Checking redis cache for email update: {}" + token);
        Jedis jedis = new Jedis("localhost");
        try {
            if (jedis.exists(token)) {
                logger.info("Redis cache for email update exists: {}" + token);
                return true;
            } else {
                logger.info("Redis cache for email update doesn't exist: {}" + token);
                return false;
            }
        } catch (Exception e) {
            logger.error("Failed to check redis cache for email update", e);
        }
        return false;
    }

    public String GetEmailByToken(String token) {
        Jedis jedis = new Jedis("localhost");
        try {
            return jedis.get(token);
        } catch (Exception e) {
            // Handle exceptions
        } finally {
            jedis.close(); // Close the Jedis connection
        }
        return null;
    }
    public void DeleteEmailByToken(String token) {
        Jedis jedis = new Jedis("localhost");
        try {
            jedis.del(token); // Delete the token-email pair from Redis
        } catch (Exception e) {
            // Handle exceptions
        } finally {
            jedis.close(); // Close the Jedis connection
        }
    }
    public void SetAllGameIconsCache(List<GameIconVO> gameIconVOList){
        logger.info("Setting up all game icons cache");
        Jedis jedis = new Jedis("localhost");
        try{
            String gameIconVOList_json = objectMapper.writeValueAsString(gameIconVOList);
            jedis.set(ALL_GAME_ICON_KEY, gameIconVOList_json);
        }catch (Exception e){
            logger.error("Failed to set all game icons cache", e);
        }finally {
            jedis.close();
        }
    }
    public boolean CheckAllGameIconsCache(){
        logger.info("Checking all game icons cache");
        Jedis jedis = new Jedis("localhost");
        try{
            if(jedis.exists(ALL_GAME_ICON_KEY)){
                logger.info("All game icons cache exists");
                return true;
            }else{
                logger.info("All game icons cache doesn't exist");
                return false;
            }
        }catch (Exception e){
            logger.error("Failed to check all game icons cache", e);
        }finally {
            jedis.close();
        }
        return false;
    }
    public List<GameIconVO> GetAllGameIconsCache(){
        logger.info("Getting all game icons cache");
        Jedis jedis = new Jedis("localhost");
        try{
            String json = jedis.get(ALL_GAME_ICON_KEY);
            List<GameIconVO> res = objectMapper.readValue(json, List.class);
            return res;
        }catch (Exception e){
            logger.error("Failed to get all game icons cache", e);
        }finally {
            jedis.close();
        }
        return null;
    }
}

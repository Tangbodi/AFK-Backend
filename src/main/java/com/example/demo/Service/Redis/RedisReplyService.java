package com.example.demo.Service.Redis;

import com.example.demo.Constant.Enum.CountNameEnum;
import com.example.demo.Model.DTO.CommentReplyDTO;
import com.example.demo.Service.Replies.ReplyInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RedisReplyService {
    private static final Logger logger = LoggerFactory.getLogger(RedisService.class);
    @Autowired
    private RedisService redisService;
    public void HandleReplyCountStrategy(CommentReplyDTO commentReplyDTO){
        logger.info("Handling reply count strategy");
        Long postId = commentReplyDTO.getPostId();
        String countName = CountNameEnum.REPLY_COUNT.getCountName();
        String key = countName + ":::" + postId;
        String hashKey = String.valueOf(commentReplyDTO.getReplyId());
        String value = String.valueOf(commentReplyDTO.getFromUid());
        if (!redisService.MemberExists(countName, postId)) {
            //REPLY_COUNT
            redisService.AddSet(countName, postId);
        } else {
            //
        }
        //postId -> replyId -> fromUid
        redisService.AddHashSet(key, hashKey, value);
    }
}

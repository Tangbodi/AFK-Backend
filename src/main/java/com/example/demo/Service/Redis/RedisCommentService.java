package com.example.demo.Service.Redis;

import com.example.demo.Constant.Enum.CountNameEnum;
import com.example.demo.Model.DTO.CommentReplyDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RedisCommentService {
    private static final Logger logger = LoggerFactory.getLogger(RedisCommentService.class);
    @Autowired
    private RedisService redisService;
    public void HandleCommentCountStrategy(CommentReplyDTO commentReplyDTO){
        logger.info("Handling comment count strategy");
        Long postId = commentReplyDTO.getPostId();
        String countName = CountNameEnum.COMMENT_COUNT.getCountName();
        String key = countName + ":::" + postId;
        String hashKey = String.valueOf(commentReplyDTO.getCommentId());
        String value = String.valueOf(commentReplyDTO.getFromUid());
        if (!redisService.MemberExists(countName, postId)) {
            //COMMENT_COUNT
            redisService.AddSet(countName, postId);
        } else {
            //
        }
        //postId -> commentId -> fromUid
        redisService.AddHashSet(key, hashKey, value);
    }
}

package com.example.demo.Controller;

import com.example.demo.Constant.Enum.ObjectNameEnum;
import com.example.demo.Service.Redis.RedisService;
import com.example.demo.Service.Replies.ReplyInfoService;
import com.example.demo.Service.UserLikeSave.UserLikeSaveService;
import com.example.demo.Util.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
public class UpdateReplyTotalLike {
    private static final Logger logger = LoggerFactory.getLogger(UpdateReplyTotalLike.class);
    private static final String REPLY_LIKE = ObjectNameEnum.REPLY_LIKE_SET.getTypeName();
    private static final String UPDATE = "UPDATE_";
    @Autowired
    private RedisService redisService;
    @Autowired
    private UserLikeSaveService userLikeSaveService;
    @Autowired
    private ReplyInfoService replyInfoService;

    @Scheduled(fixedRate = 60000)
    @PutMapping("/update-reply-like")
    public ResponseEntity UpdateReplyTotalLike() {
        ApiResponse apiResponse;
        Integer objectCode = ObjectNameEnum.GetTypeCode(REPLY_LIKE);
        logger.info("objectCode: {}", objectCode);
        //get all object ids under objectName set in Redis
        Set<String> objectIds = redisService.GetAllSetMembers(UPDATE+REPLY_LIKE);
        if (objectIds.isEmpty()) {
            logger.info("objectIds is empty");
        } else {
            List<Long> replyIds = new ArrayList<>();
            for (String objectId : objectIds) {
                //userId,date
                //HashSet key is post_like:::postId in Redis
                Map<String, String> hashSetMap = redisService.GetHashValue(UPDATE+REPLY_LIKE + ":::" + objectId);
                hashSetMap.entrySet().stream().forEach(entry -> {
                    Long replyId = Long.valueOf(objectId);
                    String userId = entry.getKey();
                    replyIds.add(replyId);
                    redisService.DeleteMember(UPDATE+REPLY_LIKE + ":::" + objectId, userId);
                    if (redisService.NumOfMembers(objectId) == 0) {
                        redisService.RemoveHashSet(UPDATE+REPLY_LIKE, objectId);
                        logger.info("Removed hash set: {}", UPDATE+REPLY_LIKE);
                    } else {
                        //
                    }
                });
            }
            replyInfoService.CalculateReplyTotalLike(replyIds);
        }
        apiResponse = ApiResponse.success("Updated reply like status successfully");
        return ResponseEntity.status(apiResponse.getCode()).body(apiResponse);
    }
}

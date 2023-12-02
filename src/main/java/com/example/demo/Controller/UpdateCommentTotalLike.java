package com.example.demo.Controller;

import com.example.demo.Constant.Enum.ObjectNameEnum;
import com.example.demo.Service.Comments.CommentInfoService;
import com.example.demo.Service.Redis.RedisService;
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
public class UpdateCommentTotalLike {
    private static final Logger logger = LoggerFactory.getLogger(UpdateCommentTotalLike.class);
    private static final String COMMENT_LIKE = ObjectNameEnum.COMMENT_LIKE_SET.getTypeName();
    private static final String UPDATE = "UPDATE_";
    @Autowired
    private RedisService redisService;
    @Autowired
    private UserLikeSaveService userLikeSaveService;
    @Autowired
    private CommentInfoService commentInfoService;

    @Scheduled(fixedRate = 30000)
    @PutMapping("/update-comment-like")
    public ResponseEntity UpdateCommentTotalLike() {
        ApiResponse apiResponse;
        //get all post ids under POST_LIKE set in Redis

        Integer objectCode = ObjectNameEnum.GetTypeCode(COMMENT_LIKE);
        logger.info("objectCode: {}", objectCode);
        //get all object ids under objectName set in Redis
        Set<String> objectIds = redisService.GetAllSetMembers(UPDATE+COMMENT_LIKE);
        if (objectIds.isEmpty()) {
            logger.info("objectIds is empty");
        } else {
            List<Long> commentIds = new ArrayList<>();
            for (String objectId : objectIds) {
                //userId,date
                //HashSet key is post_like:::postId in Redis
                Map<String, String> hashSetMap = redisService.GetHashValue(UPDATE+COMMENT_LIKE + ":::" + objectId);
                hashSetMap.entrySet().stream().forEach(entry -> {
                    Long commentId = Long.valueOf(objectId);
                    String userId = entry.getKey();
                    commentIds.add(commentId);
                    redisService.DeleteMember(UPDATE+COMMENT_LIKE + ":::" + objectId, userId);
                    if (redisService.NumOfMembers(objectId) == 0) {
                        redisService.RemoveHashSet(UPDATE+COMMENT_LIKE, objectId);
                        logger.info("Removed hash set: {}", UPDATE+COMMENT_LIKE);
                    } else {
                        //
                    }
                });
            }
            commentInfoService.CalculateCommentTotalLike(commentIds);
        }
        apiResponse = ApiResponse.success("Updated comment like count successfully");
        return ResponseEntity.status(apiResponse.getCode()).body(apiResponse);
    }
}

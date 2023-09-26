package com.example.demo.Controller;

import com.example.demo.Constant.Enum.CountNameEnum;
import com.example.demo.Service.Posts.PostInfoService;
import com.example.demo.Service.Redis.RedisService;
import com.example.demo.Util.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
public class UpdateCommentReplyCountController {
    private static final Logger logger = LoggerFactory.getLogger(UpdateCommentReplyCountController.class);
    private static final String COMMENT_COUNT = CountNameEnum.COMMENT_COUNT.getCountName();
    private static final String REPLY_COUNT = CountNameEnum.REPLY_COUNT.getCountName();
    private static final List<String> COUNT_NAME_LIST = Arrays.asList(COMMENT_COUNT, REPLY_COUNT);
    @Autowired
    private RedisService redisService;
    @Autowired
    private PostInfoService postInfoService;

//    @Scheduled(fixedRate = 9000)
    @PutMapping("/update-comment-reply-count")
    public ResponseEntity UpdateCommentReplyCountForPost() {
        ApiResponse apiResponse;
        for (String countName : COUNT_NAME_LIST) {
            //get all post ids under COMMENT_COUNT set in Redis
            Set<String> postIds = redisService.GetAllSetMembers(countName);
            if (postIds.isEmpty()) {
                logger.info("postIds is empty");
            } else {
                for (String postId : postIds) {
                    Map<String, String> hashSetMap = redisService.GetHashValue(countName + ":::" + postId);
                    Integer total = (int) redisService.GetHashSetSize(countName + ":::" + postId);
                    postInfoService.UpdatePostCommentReplyCount(Long.valueOf(postId), total);
                    hashSetMap.entrySet().stream().forEach(entry -> {
                        String commentReplyId = entry.getKey();
                        logger.info("HashSet Size: {}", total);
                        redisService.DeleteMember(countName + ":::" + postId, commentReplyId);
                        if (redisService.NumOfMembers(postId) == 0) {
                            redisService.RemoveHashSet(countName, postId);
                            logger.info("Removed hash set: {}", countName);
                        } else {
                            //
                        }
                    });
                }
            }
        }
        apiResponse = ApiResponse.success("Updated comment reply count successfully");
        return ResponseEntity.status(apiResponse.getCode()).body(apiResponse);
    }
}

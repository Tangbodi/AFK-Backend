package com.example.demo.Controller;

import com.example.demo.Constant.Enum.ObjectNameEnum;
import com.example.demo.Service.Posts.PostInfoService;
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
public class UpdatePostTotalSave {
    private static final Logger logger = LoggerFactory.getLogger(UpdatePostTotalSave.class);
    private static final String POST_SAVE = ObjectNameEnum.POST_SAVE_SET.getTypeName();
    private static final String UPDATE = "UPDATE_";

    @Autowired
    private RedisService redisService;
    @Autowired
    private UserLikeSaveService userLikeSaveService;
    @Autowired
    private PostInfoService postInfoService;

    @Scheduled(fixedRate = 30000)
    @PutMapping("/update-post-save")
    public ResponseEntity UpdatePostTotalSave() {
        ApiResponse apiResponse;
        //get all post ids under POST_LIKE set in Redis
        Integer objectCode = ObjectNameEnum.GetTypeCode(POST_SAVE);
        logger.info("objectCode: {}", objectCode);
        //get all object ids under objectName set in Redis
        Set<String> objectIds = redisService.GetAllSetMembers(UPDATE+POST_SAVE);
        if (objectIds.isEmpty()) {
            logger.info("objectIds is empty");
        } else {
            List<Long> postIds = new ArrayList<>();
            for (String objectId : objectIds) {
                //userId,date
                //HashSet key is post_like:::postId in Redis
                Map<String, String> hashSetMap = redisService.GetHashValue(UPDATE+POST_SAVE + ":::" + objectId);
                hashSetMap.entrySet().stream().forEach(entry -> {
                    Long postId = Long.valueOf(objectId);
                    String userId = entry.getKey();
                    postIds.add(postId);
                    redisService.DeleteMember(UPDATE+POST_SAVE + ":::" + objectId, userId);
                    if (redisService.NumOfMembers(objectId) == 0) {
                        redisService.RemoveHashSet(UPDATE+POST_SAVE, objectId);
                        logger.info("Removed hash set: {}", UPDATE+POST_SAVE);
                    } else {
                        //
                    }
                });
            }
            postInfoService.CalculatePostTotalSave(postIds);
        }
        apiResponse = ApiResponse.success("Updated post save status successfully");
        return ResponseEntity.status(apiResponse.getCode()).body(apiResponse);
    }
}

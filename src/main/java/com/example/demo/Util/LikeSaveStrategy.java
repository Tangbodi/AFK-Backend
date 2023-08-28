package com.example.demo.Util;

import com.example.demo.Enum.ObjectNameEnum;
import com.example.demo.Enum.StatusEnum;
import com.example.demo.Model.DTO.UserLikeSaveDTO;
import com.example.demo.Service.Redis.RedisLikeSaveService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LikeSaveStrategy {
    private static final Logger logger = LoggerFactory.getLogger(LikeSaveStrategy.class);

    @Autowired
    protected RedisLikeSaveService redisLikeSaveService;

    public void StartStrategy(UserLikeSaveDTO userLikeSaveDTO) {
        // 1 or 0
        Integer status = userLikeSaveDTO.getStatus();
        Long userId = userLikeSaveDTO.getUserId();
        //post_like/comment_like/reply_like/post_save
        String typeNameInSet = ObjectNameEnum.GetTypeName(userLikeSaveDTO.getTypeId());
        //postId/commentId/replyId
        Long objectId = userLikeSaveDTO.getObjectId();

//        Long snowFlakeId = Snowflake.generateUniqueId();
        String key = typeNameInSet + ":::" + objectId;
        String hashKey = String.valueOf(userId);
        String value = String.valueOf(userLikeSaveDTO.getCreatedAt());
        //if status is 1
        if (status == StatusEnum.TRUE.getCode()) {
            //if the Type name doesn't exist then add to set
            if (!redisLikeSaveService.MemberExists(typeNameInSet, objectId)) {
                redisLikeSaveService.AddSet(typeNameInSet, objectId);
            }
            redisLikeSaveService.AddHashSet(key, hashKey, value);
        } else {
            //if status is 0, remove from hash set
            redisLikeSaveService.DeleteMember(key, hashKey);
            if (redisLikeSaveService.NumOfMembers(key) == 0) {
                redisLikeSaveService.RemoveHashSet(typeNameInSet, objectId);
            }
        }
    }

    public void like(UserLikeSaveDTO UserLikeDTO) {
    }

    public String getTypeName() {
        return null;
    }

}

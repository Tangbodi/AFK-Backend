package com.example.demo.Controller;

import com.example.demo.Constant.Enum.ObjectNameEnum;
import com.example.demo.Model.DTO.ObjectUserDTO;
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
public class UpdateReplyLikeController {
    private static final Logger logger = LoggerFactory.getLogger(UpdateReplyLikeController.class);
    private static final String REPLY_LIKE = ObjectNameEnum.REPLY_LIKE_SET.getTypeName();

    @Autowired
    private RedisService redisService;
    @Autowired
    private UserLikeSaveService userLikeSaveService;
    @Autowired
    private ReplyInfoService replyInfoService;

//    @Scheduled(fixedRate = 4000)
    @PutMapping("/update-reply-like")
    public ResponseEntity UpdateReplyLike() {
        ApiResponse apiResponse;
        Integer objectCode = ObjectNameEnum.GetTypeCode(REPLY_LIKE);
        logger.info("objectCode: {}", objectCode);
        //get all object ids under objectName set in Redis
        Set<String> objectIds = redisService.GetAllSetMembers(REPLY_LIKE);
        if (objectIds.isEmpty()) {
            logger.info("objectIds is empty");
        } else {
            List<ObjectUserDTO> objectUserDTOList = new ArrayList<>();
            for (String objectId : objectIds) {
                //userId,date
                //HashSet key is post_like:::postId in Redis
                Map<String, String> hashSetMap = redisService.GetHashValue(REPLY_LIKE + ":::" + objectId);
                hashSetMap.entrySet().stream().forEach(entry -> {
                    ObjectUserDTO objectUserDTO = new ObjectUserDTO();
                    objectUserDTO.setObjectId(Long.valueOf(objectId));
                    String userId = entry.getKey();
                    objectUserDTO.setUserId(Long.valueOf(userId));
                    objectUserDTO.setStatus(Integer.valueOf(entry.getValue()));
                    objectUserDTOList.add(objectUserDTO);
                    redisService.DeleteMember(REPLY_LIKE + ":::" + objectId, userId);
                    if (redisService.NumOfMembers(objectId) == 0) {
                        redisService.RemoveHashSet(REPLY_LIKE, objectId);
                        logger.info("Removed hash set: {}", REPLY_LIKE);
                    } else {
                        //
                    }
                });
            }
            userLikeSaveService.SetUserLikeReply(objectUserDTOList);
            replyInfoService.CalculateReplyTotalLike(objectUserDTOList);
        }
        apiResponse = ApiResponse.success("Updated reply like status successfully");
        return ResponseEntity.status(apiResponse.getCode()).body(apiResponse);
    }
}

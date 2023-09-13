package com.example.demo.Controller;

import com.example.demo.Constant.Enum.ObjectNameEnum;
import com.example.demo.Model.DTO.ObjectUserDTO;
import com.example.demo.Service.Comments.CommentInfoService;
import com.example.demo.Service.Posts.PostInfoService;
import com.example.demo.Service.Redis.RedisLikeSaveService;
import com.example.demo.Service.Replies.ReplyInfoService;
import com.example.demo.Service.UserFavoriteGame.UserFavoriteGameService;
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
public class ReplyLikeController {
    private static final Logger logger = LoggerFactory.getLogger(ReplyLikeController.class);
    private static final String REPLY_LIKE = ObjectNameEnum.REPLY_LIKE_SET.getTypeName();

    @Autowired
    private RedisLikeSaveService redisLikeSaveService;
    @Autowired
    private UserLikeSaveService userLikeSaveService;
    @Autowired
    private ReplyInfoService replyInfoService;

    @Scheduled(fixedRate = 4000)
    @PutMapping("/update-reply-like")
    public ResponseEntity UpdateReplyLike() {
        ApiResponse apiResponse;
        Integer objectCode = ObjectNameEnum.GetTypeCode(REPLY_LIKE);
        logger.info("objectCode: {}", objectCode);
        //get all object ids under objectName set in Redis
        Set<String> objectIds = redisLikeSaveService.GetAllSetMembers(REPLY_LIKE);
        if (objectIds.isEmpty()) {
            logger.info("objectIds is empty");
        } else {
            logger.info("objectIds: {}", objectIds);
            List<ObjectUserDTO> objectUserDTOList = new ArrayList<>();
            for (String objectId : objectIds) {
                //userId,date
                //HashSet key is post_like:::postId in Redis
                Map<String, String> hashSetMap = redisLikeSaveService.GetHashValue(REPLY_LIKE + ":::" + objectId);
                hashSetMap.entrySet().stream().forEach(entry -> {
                    ObjectUserDTO objectUserDTO = new ObjectUserDTO();
                    objectUserDTO.setObjectId(Long.valueOf(objectId));
                    String userId = entry.getKey();
                    objectUserDTO.setUserId(Long.valueOf(userId));
                    objectUserDTO.setStatus(Integer.valueOf(entry.getValue()));
                    objectUserDTOList.add(objectUserDTO);
                    redisLikeSaveService.DeleteMember(REPLY_LIKE + ":::" + objectId, userId);
                    if (redisLikeSaveService.NumOfMembers(objectId) == 0) {
                        redisLikeSaveService.RemoveHashSet(REPLY_LIKE, objectId);
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

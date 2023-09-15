package com.example.demo.Controller;

import com.example.demo.Constant.Enum.ObjectNameEnum;
import com.example.demo.Model.DTO.ObjectUserDTO;
import com.example.demo.Service.Comments.CommentInfoService;
import com.example.demo.Service.Redis.RedisLikeSaveService;
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
public class UpdateCommentLikeController {
    private static final Logger logger = LoggerFactory.getLogger(UpdateCommentLikeController.class);
    private static final String COMMENT_LIKE = ObjectNameEnum.COMMENT_LIKE_SET.getTypeName();

    @Autowired
    private RedisLikeSaveService redisLikeSaveService;
    @Autowired
    private UserLikeSaveService userLikeSaveService;
    @Autowired
    private CommentInfoService commentInfoService;

    @Scheduled(fixedRate = 4000)
    @PutMapping("/update-comment-like")
    public ResponseEntity UpdateCommentLike() {
        ApiResponse apiResponse;
        //get all post ids under POST_LIKE set in Redis

        Integer objectCode = ObjectNameEnum.GetTypeCode(COMMENT_LIKE);
        logger.info("objectCode: {}", objectCode);
        //get all object ids under objectName set in Redis
        Set<String> objectIds = redisLikeSaveService.GetAllSetMembers(COMMENT_LIKE);
        if (objectIds.isEmpty()) {
            logger.info("objectIds is empty");
        } else {
            logger.info("objectIds: {}", objectIds);
            List<ObjectUserDTO> objectUserDTOList = new ArrayList<>();
            for (String objectId : objectIds) {
                //userId,date
                //HashSet key is post_like:::postId in Redis
                Map<String, String> hashSetMap = redisLikeSaveService.GetHashValue(COMMENT_LIKE + ":::" + objectId);
                hashSetMap.entrySet().stream().forEach(entry -> {
                    ObjectUserDTO objectUserDTO = new ObjectUserDTO();
                    objectUserDTO.setObjectId(Long.valueOf(objectId));
                    String userId = entry.getKey();
                    objectUserDTO.setUserId(Long.valueOf(userId));
                    objectUserDTO.setStatus(Integer.valueOf(entry.getValue()));
                    objectUserDTOList.add(objectUserDTO);
                    redisLikeSaveService.DeleteMember(COMMENT_LIKE + ":::" + objectId, userId);
                    if (redisLikeSaveService.NumOfMembers(objectId) == 0) {
                        redisLikeSaveService.RemoveHashSet(COMMENT_LIKE, objectId);
                        logger.info("Removed hash set: {}", COMMENT_LIKE);
                    } else {
                        //
                    }
                });
            }
            userLikeSaveService.SetUserLikeComment(objectUserDTOList);
            commentInfoService.CalculateCommentTotalLike(objectUserDTOList);
        }
        apiResponse = ApiResponse.success("Updated comment like status successfully");
        return ResponseEntity.status(apiResponse.getCode()).body(apiResponse);
    }
}

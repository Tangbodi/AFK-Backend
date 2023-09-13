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
public class PostSaveController {
    private static final Logger logger = LoggerFactory.getLogger(PostSaveController.class);
    private static final String POST_SAVE = ObjectNameEnum.POST_SAVE_SET.getTypeName();
    @Autowired
    private RedisLikeSaveService redisLikeSaveService;
    @Autowired
    private UserLikeSaveService userLikeSaveService;
    @Autowired
    private PostInfoService postInfoService;

    @Scheduled(fixedRate = 4000)
    @PutMapping("/update-post-save")
    public ResponseEntity UpdatePostSave() {
        ApiResponse apiResponse;
        //get all post ids under POST_LIKE set in Redis
        Integer objectCode = ObjectNameEnum.GetTypeCode(POST_SAVE);
        logger.info("objectCode: {}", objectCode);
        //get all object ids under objectName set in Redis
        Set<String> objectIds = redisLikeSaveService.GetAllSetMembers(POST_SAVE);
        if (objectIds.isEmpty()) {
            logger.info("objectIds is empty");
        } else {
            logger.info("objectIds: {}", objectIds);
            List<ObjectUserDTO> objectUserDTOList = new ArrayList<>();
            for (String objectId : objectIds) {
                //userId,date
                //HashSet key is post_like:::postId in Redis
                Map<String, String> hashSetMap = redisLikeSaveService.GetHashValue(POST_SAVE + ":::" + objectId);
                hashSetMap.entrySet().stream().forEach(entry -> {
                    ObjectUserDTO objectUserDTO = new ObjectUserDTO();
                    objectUserDTO.setObjectId(Long.valueOf(objectId));
                    String userId = entry.getKey();
                    objectUserDTO.setUserId(Long.valueOf(userId));
                    objectUserDTO.setStatus(Integer.valueOf(entry.getValue()));
                    objectUserDTOList.add(objectUserDTO);
                    redisLikeSaveService.DeleteMember(POST_SAVE + ":::" + objectId, userId);
                    if (redisLikeSaveService.NumOfMembers(objectId) == 0) {
                        redisLikeSaveService.RemoveHashSet(POST_SAVE, objectId);
                    } else {
                        //
                    }
                });
            }
            userLikeSaveService.SetUserSavePost(objectUserDTOList);
            postInfoService.CalculatePostTotalSave(objectUserDTOList);
        }
        apiResponse = ApiResponse.success("Updated post save status successfully");
        return ResponseEntity.status(apiResponse.getCode()).body(apiResponse);
    }
}

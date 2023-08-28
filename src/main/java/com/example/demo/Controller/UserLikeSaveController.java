package com.example.demo.Controller;

import com.example.demo.Enum.ObjectNameEnum;
import com.example.demo.Enum.ReturnCode;
import com.example.demo.Model.DTO.ObjectUserDTO;
import com.example.demo.Model.DTO.UserLikeSaveDTO;
import com.example.demo.Service.MQ.MQSender;
import com.example.demo.Service.Redis.RedisLikeSaveService;
import com.example.demo.Service.UserLikeSave.UserLikeSaveService;
import com.example.demo.Util.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.jms.JMSException;
import javax.servlet.http.HttpSession;
import java.time.Instant;
import java.util.*;

@RestController
@Validated
@RequestMapping("/all-games-genres")
public class UserLikeSaveController {
    private static final Logger logger = LoggerFactory.getLogger(UserLikeSaveController.class);
    private static final String POST_LIKE = "post_like";
    private static final String COMMENT_LIKE = "comment_like";
    private static final String REPLY_LIKE = "reply_like";
    private static final String POST_SAVE = "post_save";
    private static final List<String> OBJECT_NAME_LIST = Arrays.asList(POST_LIKE, COMMENT_LIKE, REPLY_LIKE, POST_SAVE);
    @Autowired
    private RedisLikeSaveService redisLikeSaveService;
    @Autowired
    private UserLikeSaveService userLikeSaveService;
    @Autowired
    private MQSender mqSender;

    @PostMapping("/genre/user-like-save")
    public ResponseEntity SetUserLikeSavePost(@Validated @RequestBody UserLikeSaveDTO userLikeSaveDTO, HttpSession session) throws JMSException, InterruptedException {
        ApiResponse apiResponse;
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            apiResponse = ApiResponse.error(ReturnCode.RC401.getCode(), "Sign in to make your opinion count");
            return ResponseEntity.status(apiResponse.getCode()).body(apiResponse);
        } else {
            apiResponse = ApiResponse.success(null);
            mqSender.SendMessage(userLikeSaveDTO, userId);
        }
        return ResponseEntity.status(apiResponse.getCode()).body(apiResponse);
    }

    @GetMapping("/genre/like-save-saving")
    public ResponseEntity StoreAllLikeSaveStatusIntoDB() {
        ApiResponse apiResponse;
        //get all post ids under POST_LIKE set in Redis
        for (String objectName : OBJECT_NAME_LIST) {
            Integer objectCode = ObjectNameEnum.GetType(objectName);
            logger.info("objectCode: {}", objectCode);
            //get all object ids under objectName set in Redis
            Set<String> objectIds = redisLikeSaveService.GetAllSetMembers(objectName);
            logger.info("objectIds: {}", objectIds);
            List<ObjectUserDTO> objectUserDTOList = new ArrayList<>();
            for (String objectId : objectIds) {
                //userId,date
                //HashSet key is post_like:::postId in Redis
                Map<String, String> hashSetMap = redisLikeSaveService.GetHashValue(objectName + ":::" + objectId);
                hashSetMap.entrySet().stream().forEach(entry -> {
                    ObjectUserDTO objectUserDTO = new ObjectUserDTO();
                    objectUserDTO.setObjectId(Long.valueOf(objectId));
                    String userId = entry.getKey();
                    objectUserDTO.setUserId(Long.valueOf(userId));
                    objectUserDTO.setCreatedAt(Instant.parse(entry.getValue()));
                    objectUserDTOList.add(objectUserDTO);

                    redisLikeSaveService.DeleteMember(objectName + ":::" + objectId, userId);
                    if (redisLikeSaveService.NumOfMembers(objectId) == 0) {
                        redisLikeSaveService.RemoveHashSet(objectName, objectId);
                    } else {
                        //
                    }

                });
            }
            if (objectCode == 0) {
                userLikeSaveService.SetUserLikePost(objectUserDTOList);
            } else if (objectCode == 1) {
                userLikeSaveService.SetUserLikeComment(objectUserDTOList);
            } else if (objectCode == 2) {
                userLikeSaveService.SetUserLikeReply(objectUserDTOList);
            } else if (objectCode == 3) {
                userLikeSaveService.SetUserSavePost(objectUserDTOList);
            } else {
                //
            }
        }
        apiResponse = ApiResponse.success(null);
        return ResponseEntity.status(apiResponse.getCode()).body(apiResponse);
    }
}

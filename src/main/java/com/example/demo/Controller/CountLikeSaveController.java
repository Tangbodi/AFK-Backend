package com.example.demo.Controller;

import com.example.demo.Constant.Enum.CountNameEnum;
import com.example.demo.Constant.Enum.ObjectNameEnum;
import com.example.demo.Model.DTO.ObjectUserDTO;
import com.example.demo.Service.Comments.CommentInfoService;
import com.example.demo.Service.Posts.PostInfoService;
import com.example.demo.Service.Redis.RedisLikeSaveService;
import com.example.demo.Service.Replies.ReplyInfoService;
import com.example.demo.Service.UserFavoriteGame.UserFavoriteGameService;
import com.example.demo.Service.UserLikeSave.UserLikeSaveService;
import com.example.demo.Util.ApiResponse;
import org.apache.activemq.thread.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.*;

@RestController
public class CountLikeSaveController {
    private static final Logger logger = LoggerFactory.getLogger(CountLikeSaveController.class);
    private static final String POST_LIKE = ObjectNameEnum.POST_LIKE_SET.getTypeName();
    private static final String COMMENT_LIKE = ObjectNameEnum.COMMENT_LIKE_SET.getTypeName();
    private static final String REPLY_LIKE = ObjectNameEnum.REPLY_LIKE_SET.getTypeName();
    private static final String POST_SAVE = ObjectNameEnum.POST_SAVE_SET.getTypeName();
    private static final String GAME_SAVE = ObjectNameEnum.GAME_SAVE_SET.getTypeName();
    private static final List<String> OBJECT_NAME_LIST = Arrays.asList(POST_LIKE, COMMENT_LIKE, REPLY_LIKE, POST_SAVE, GAME_SAVE);
    private static final String COMMENT_COUNT = CountNameEnum.COMMENT_COUNT.getCountName();
    private static final String REPLY_COUNT = CountNameEnum.REPLY_COUNT.getCountName();
    private static final List<String> COUNT_NAME_LIST = Arrays.asList(COMMENT_COUNT, REPLY_COUNT);

    @Autowired
    private RedisLikeSaveService redisLikeSaveService;
    @Autowired
    private UserLikeSaveService userLikeSaveService;
    @Autowired
    private PostInfoService postInfoService;
    @Autowired
    private CommentInfoService commentInfoService;
    @Autowired
    private ReplyInfoService replyInfoService;
    @Autowired
    private UserFavoriteGameService userFavoriteGameService;

//    @Scheduled(fixedRate = 6000) // every 6 seconds
    @PutMapping("/update-like-save-status-and-count")
    public ResponseEntity UpdateLikeSaveStatusAndCount() {
        ApiResponse apiResponse;
        //get all post ids under POST_LIKE set in Redis
        for (String objectName : OBJECT_NAME_LIST) {
            Integer objectCode = ObjectNameEnum.GetTypeCode(objectName);
            logger.info("objectCode: {}", objectCode);
            //get all object ids under objectName set in Redis
            Set<String> objectIds = redisLikeSaveService.GetAllSetMembers(objectName);
            if(objectIds.isEmpty()){
                logger.info("objectIds is empty");
                continue;
            } else {
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
                    //update like status in database
                    userLikeSaveService.SetUserLikePost(objectUserDTOList);
                    //update total like count in database
                    postInfoService.CalculatePostTotalLike(objectUserDTOList);
                } else if (objectCode == 1) {
                    userLikeSaveService.SetUserLikeComment(objectUserDTOList);
                    commentInfoService.CalculateCommentTotalLike(objectUserDTOList);
                } else if (objectCode == 2) {
                    userLikeSaveService.SetUserLikeReply(objectUserDTOList);
                    replyInfoService.CalculateReplyTotalLike(objectUserDTOList);
                } else if(objectCode == 3) {
                    userLikeSaveService.SetUserSavePost(objectUserDTOList);
                    postInfoService.CalculatePostTotalSave(objectUserDTOList);
                } else { //objectCode == 4
                    userFavoriteGameService.SetUserFavoriteGame(objectUserDTOList);
                }
            }
        }
        apiResponse = ApiResponse.success("Updated like save status successfully");
        return ResponseEntity.status(apiResponse.getCode()).body(apiResponse);
    }
//    @Scheduled(fixedRate = 6000)
    @PutMapping("/update-comment-reply-count")
    public ResponseEntity UpdateCommentReplyCountForPost() {
        ApiResponse apiResponse;
        for (String countName : COUNT_NAME_LIST) {
            //get all post ids under COMMENT_COUNT set in Redis
            Set<String> postIds = redisLikeSaveService.GetAllSetMembers(countName);
            if (postIds.isEmpty()) {
                logger.info("postIds is empty");
                continue;
            } else {
                logger.info("postIds: {}", postIds);
                for (String postId : postIds) {
                    Map<String, String> hashSetMap = redisLikeSaveService.GetHashValue(countName + ":::" + postId);
                    Integer total = (int) redisLikeSaveService.GetHashSetSize(countName + ":::" + postId);
                    postInfoService.UpdatePostCommentReplyCount(Long.valueOf(postId), total);
                    hashSetMap.entrySet().stream().forEach(entry -> {
                        String commentReplyId = entry.getKey();
                        logger.info("HashSet Size: {}", total);
                        redisLikeSaveService.DeleteMember(countName + ":::" + postId, commentReplyId);
                        if (redisLikeSaveService.NumOfMembers(postId) == 0) {
                            redisLikeSaveService.RemoveHashSet(countName, postId);
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

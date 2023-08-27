package com.example.demo.Controller;

import com.example.demo.Model.DTO.UserLikeDTO;
import com.example.demo.Model.DTO.UserLikesSavesPostDTO;
import com.example.demo.Model.Entity.UsersFavoritePostId;
import com.example.demo.Repository.PostImageRepository;
import com.example.demo.Service.Posts.PostImageService;
import com.example.demo.Service.Posts.PostService;
import com.example.demo.Service.Redis.RedisLikeSaveService;
import com.example.demo.Service.UserFavoritePost.UserFavoritePostService;
import com.example.demo.Util.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@RestController
public class TestController {
    private static final Logger logger = LoggerFactory.getLogger(TestController.class);
    @Autowired
    private RedisLikeSaveService redisLikeSaveService;
    @Autowired
    private UserFavoritePostService userFavoritePostService;
    @Autowired
    private KafkaTemplate<Object, Object> template;

    @PostMapping("/getData")
    public ResponseEntity getData() {
        ApiResponse apiResponse;
        Map<String,Map<String,String>> res = new HashMap();
        Set<String> postIds =  redisLikeSaveService.GetAllSetMembers("post_like");
        List<UsersFavoritePostId> postUserIdMap = new ArrayList<>();
        for(String postId: postIds){
            //userId,date
            Map<String, String> fieldValue = redisLikeSaveService.GetHashValue("post_like:::"+postId);
            fieldValue.entrySet().stream().forEach(entry -> {
                UsersFavoritePostId usersFavoritePostId = new UsersFavoritePostId();
                usersFavoritePostId.setPostId(Long.valueOf(postId));
                usersFavoritePostId.setUserId(Long.valueOf(entry.getKey()));
                postUserIdMap.add(usersFavoritePostId);
            });
        }
        for(UsersFavoritePostId usersFavoritePostId: postUserIdMap){
            userFavoritePostService.SetUserLikePost(usersFavoritePostId);
        }
        apiResponse = ApiResponse.success(postUserIdMap);
        return ResponseEntity.status(apiResponse.getCode()).body(apiResponse);
    }

}

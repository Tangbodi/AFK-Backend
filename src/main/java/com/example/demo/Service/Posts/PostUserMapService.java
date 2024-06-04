package com.example.demo.Service.Posts;

import com.example.demo.Mapper.Repository.PostUserMapRepository;
import com.example.demo.Model.DTO.PostDTO;
import com.example.demo.Model.Entity.PostsUsersMap;
import com.example.demo.Model.Entity.PostsUsersMapId;
import com.example.demo.Model.VO.PostInfoVO;
import com.example.demo.Util.DateTimeConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class PostUserMapService {
    private static final Logger logger = LoggerFactory.getLogger(PostUserMapService.class);
    @Autowired
    private PostUserMapRepository postUserMapRepository;

    @Async("MultiExecutor")
    @Transactional
    public void SetPostUserMap(PostDTO postDTO) {
        logger.info("Setting post user map: {}");
        try {
            PostsUsersMapId postsUsersMapId = new PostsUsersMapId();
            PostsUsersMap postsUsersMap = new PostsUsersMap();
            postsUsersMapId.setPostId(postDTO.getPostId());
            postsUsersMapId.setUserId(postDTO.getUserId());
            postsUsersMap.setId(postsUsersMapId);
            postsUsersMap.setCreatedAt(postDTO.getCreatedAt());
            postsUsersMap.setModifiedAt(postDTO.getCreatedAt());
            if (postUserMapRepository.save(postsUsersMap) != null) {
                logger.info("Post user map saved successfully: {}");
            } else {
                logger.info("Failed to save post user map: {}");
            }
        } catch (Exception e) {
            logger.error("Failed to set post user map: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to set post user map " + e); // Rethrow the exception to trigger rollback
        }
    }

    public List<PostInfoVO> FindUserPostHistory(Long userId) {
        logger.info("Getting all posts by user ID");
        try {
            List<Map<Short, Object>> allPostsByUserId = postUserMapRepository.findPostUserMapByUserId(userId);
            if (!allPostsByUserId.isEmpty()) {
                logger.info("Got all posts by user ID");
                return TransferToPostHistoryVO(allPostsByUserId);
            } else {
                logger.info("No posts found by user ID");
                return Collections.emptyList();
            }
        } catch (Exception e) {
            logger.error("Failed to get all posts by user ID", e);
            return Collections.emptyList();
        }
    }

    private static List<PostInfoVO> TransferToPostHistoryVO(List<Map<Short, Object>> allPostsByUserId) {
        logger.info("Transferring all posts by user ID to VO");
        List<PostInfoVO> postHistoryVOList = new ArrayList<>();
        try {
            for (Map<Short, Object> map : allPostsByUserId) {
                PostInfoVO postHistoryVO = new PostInfoVO();
                postHistoryVO.setPostId(String.valueOf(map.get("post_id")));
                postHistoryVO.setTitle(String.valueOf(map.get("title")));
                postHistoryVO.setUsername(String.valueOf(map.get("username")));
                postHistoryVO.setView(String.valueOf(map.get("view")));
                postHistoryVO.setReply(String.valueOf(map.get("comment_reply")));
                postHistoryVO.setLike(String.valueOf(map.get("like")));
                postHistoryVO.setSave(String.valueOf(map.get("save")));
                String formattedDateTime = DateTimeConverter.DateTimeConvertFromString(String.valueOf(map.get("created_at")));
                postHistoryVO.setCreatedAt(formattedDateTime);
                postHistoryVOList.add(postHistoryVO);
            }
        } catch (Exception e) {
            logger.error("Failed to transfer all posts by user ID to VO", e);
            return Collections.emptyList();
        }
        return postHistoryVOList;
    }

}

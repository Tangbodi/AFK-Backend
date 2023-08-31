package com.example.demo.Service.Posts;

import com.example.demo.Model.DTO.PostDTO;
import com.example.demo.Model.Entity.PostsGamesMap;
import com.example.demo.Mapper.Repository.PostGameMapRepository;
import com.example.demo.Model.VO.LatestPostVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class PostGameMapService {
    private static final Logger logger = LoggerFactory.getLogger(PostGameMapService.class);

    @Autowired
    private PostGameMapRepository postGameMapRepository;


    public List<LatestPostVO> ShowLatestPosts() {
        logger.info("Showing latest posts");
        try {
            List<Map<String, Object>> postsGamesMaps = postGameMapRepository.findLatestPostsGamesMap();
            if (!postsGamesMaps.isEmpty()) {
                logger.info("Latest posts found");
                return TransferToLatestPostVO(postsGamesMaps);
            } else {
                logger.info("No latest posts found");
            }
        } catch (Exception e) {
            logger.error("Failed to show latest posts", e.getMessage(), e);
        }
        return Collections.emptyList();
    }

    private static List<LatestPostVO> TransferToLatestPostVO(List<Map<String, Object>> postsGamesMaps) {
        logger.info("Transferring to latest post VO");
        List<LatestPostVO> latestPostVOList = new ArrayList<>();
        for (Map<String, Object> map : postsGamesMaps) {
            try {
                LatestPostVO latestPostVO = new LatestPostVO();
                latestPostVO.setPostId(map.get("post_id").toString());
                latestPostVO.setTitle((String) map.get("title"));
                latestPostVO.setGameName((String) map.get("game_name"));
                Timestamp timestamp = (Timestamp) map.get("created_at");
                latestPostVO.setCreatedAt(timestamp.toInstant());
                latestPostVOList.add(latestPostVO);
            } catch (Exception e) {
                logger.error("Failed to transfer to latest post VO",  e.getMessage(), e);
            }
        }
        return latestPostVOList;
    }

    @Async("MultiExecutor")
    @Transactional
    public void SetPostGameMap(PostDTO postDTO) {
        logger.info("Setting post game map: {}");
        try {
            PostsGamesMap postsGamesMap = new PostsGamesMap();
            postsGamesMap.setId(postDTO.getPostId());
            postsGamesMap.setGameId(postDTO.getGameId());
            postsGamesMap.setGenreId(postDTO.getGenreId());
            postsGamesMap.setCreatedAt(postDTO.getCreatedAt());
            postsGamesMap.setModifiedAt(postDTO.getCreatedAt());
            if (postGameMapRepository.save(postsGamesMap) != null) {
                logger.info("Post game map saved successfully: {}");
            } else {
                logger.info("Failed to save post game map: {}");
            }
        } catch (Exception e) {
            logger.error("Failed to set post game map: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to set post game map " +e); // Rethrow the exception to trigger rollback
        }
    }
}


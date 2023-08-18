package com.example.demo.Service.Posts;

import com.example.demo.Mapper.Repository.PostGameMapRepository;
import com.example.demo.Model.VO.LatestPostVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
            List<Map<Short, Object>> postsGamesMaps = postGameMapRepository.findLatestPostsGamesMap();
            if (!postsGamesMaps.isEmpty()) {
                logger.info("Latest posts found");
                return TransferToLatestPostVO(postsGamesMaps);
            } else {
                logger.info("No latest posts found");
            }
        } catch (Exception e) {
            logger.error("Failed to show latest posts", e);
        }
        return Collections.emptyList();
    }

    private static List<LatestPostVO> TransferToLatestPostVO(List<Map<Short, Object>> postsGamesMaps) {
        logger.info("Transferring to latest post VO");
        List<LatestPostVO> latestPostVOList = new ArrayList<>();
        for (Map<Short, Object> map : postsGamesMaps) {
            try {
                LatestPostVO latestPostVO = new LatestPostVO();
                latestPostVO.setPostId((String) map.get("post_id"));
                latestPostVO.setTitle((String) map.get("title"));
                latestPostVO.setGameName((String) map.get("game_name"));
                Timestamp timestamp = (Timestamp) map.get("created_at");
                latestPostVO.setCreatedAt(timestamp.toInstant());
                latestPostVOList.add(latestPostVO);
            } catch (Exception e) {
                logger.error("Failed to transfer to latest post VO", e);
            }
        }
        return latestPostVOList;
    }
}


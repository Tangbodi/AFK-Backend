package com.example.demo.Service.Posts;

import com.example.demo.Model.DTO.GameGenreMapIdDTO;
import com.example.demo.Model.VO.PopularPostVO;
import com.example.demo.Model.VO.PostInfoVO;
import com.example.demo.Repository.PostGameMapRepository;
import com.example.demo.Repository.PostInfoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class PostInfoService {
    private static final Logger logger = LoggerFactory.getLogger(PostInfoService.class);

    @Autowired
    private PostInfoRepository postInfoRepository;
    @Autowired
    private PostGameMapRepository postGameMapRepository;


    public List<PopularPostVO> GetMostPopularPosts() {
        logger.info("Getting most popular posts");
        try {
            List<Map<Short, Object>> popularPosts = postInfoRepository.findMostPopularPosts();
            if (!popularPosts.isEmpty()) {
                logger.info("Got most popular posts");
                return TransferToPopularPostVO(popularPosts);
            } else {
                logger.info("No popular posts found");
                return Collections.emptyList();
            }
        } catch (Exception e) {
            logger.error("Failed to get most popular posts", e);
            return Collections.emptyList();
        }
    }

    private static List<PopularPostVO> TransferToPopularPostVO(List<Map<Short, Object>> popularPosts) {
        logger.info("Transferring popular posts to VO");
        List<PopularPostVO> popularPostVOList = new ArrayList<>();
        for (Map<Short, Object> popularPost : popularPosts) {
            try {
                PopularPostVO popularPostVO = new PopularPostVO();
                popularPostVO.setPostId(popularPost.get("post_id").toString());
                popularPostVO.setTitle((String) popularPost.get("title"));
                popularPostVO.setGameName((String) popularPost.get("game_name"));
                popularPostVOList.add(popularPostVO);
            } catch (Exception e) {
                logger.error("Failed to transfer popular posts to VO", e);
                return Collections.emptyList();
            }
        }
        return popularPostVOList;
    }

    public List<PostInfoVO> GetAllPostInfoInOneGame(GameGenreMapIdDTO gameGenreMapIdDTO) {
        logger.info("Getting all post info with one game");
        try {
            List<Map<String, Object>> allPostInfoWithOneGame = postGameMapRepository.findAllPostsInOneGame(gameGenreMapIdDTO.getGameId());
            if (!allPostInfoWithOneGame.isEmpty()) {
                logger.info("Got all post info with one game");
                return TransferToPostInfoVO(allPostInfoWithOneGame);
            } else {
                logger.info("No post info with one game found");
                return Collections.emptyList();
            }
        } catch (Exception e) {
            logger.error("Failed to get all post info with one game", e);
            return Collections.emptyList();
        }
    }

    private static List<PostInfoVO> TransferToPostInfoVO(List<Map<String, Object>> allPostInfoWithOneGame) {
        logger.info("Transferring all post info with one game to VO");
        try {
            List<PostInfoVO> postInfoVOList = new ArrayList<>();
            for (Map<String, Object> map : allPostInfoWithOneGame) {
                PostInfoVO postInfoVO = new PostInfoVO();
                postInfoVO.setPostId(map.get("post_id").toString());
                postInfoVO.setTitle((String) map.get("title"));
                postInfoVO.setView((Integer) map.get("view"));
                postInfoVO.setComment((Integer) map.get("comment"));
                postInfoVO.setLike((Integer) map.get("like"));
                postInfoVO.setSave((Integer) map.get("save"));
                postInfoVO.setUsername((String) map.get("username"));
                Timestamp timestamp = (Timestamp) map.get("created_at");
                postInfoVO.setCreatedAt(timestamp.toInstant());
                postInfoVOList.add(postInfoVO);
            }
            return postInfoVOList;
        } catch (Exception e) {
            logger.error("Failed to transfer all post info with one game to VO", e);
            return Collections.emptyList();
        }
    }

    public void UpdatePostViewCount(Long postId) {
        logger.info("Updating post view count");
        postInfoRepository.findById(postId).map(postInfo -> {
            postInfo.setView(postInfo.getView() + 1);
            return postInfoRepository.save(postInfo);
        }).orElseThrow(() -> new RuntimeException("Failed to update post view count"));
    }

    public void UpdatePostLikeCount(Long postId) {
        logger.info("Updating post like count");
        postInfoRepository.findById(postId).map(postInfo -> {
            postInfo.setLike(postInfo.getLike()+1);
            return postInfoRepository.save(postInfo);
        }).orElseThrow(()-> new RuntimeException("Failed to update post like count"));
    }

    public void UpdatePostSaveCount(Long postId){
        logger.info("Updating post save count");
        postInfoRepository.findById(postId).map(postInfo -> {
            postInfo.setSave(postInfo.getSave()+1);
            return postInfoRepository.save(postInfo);
        });

    }

    public void UpdatePostCommentReplyCount(Long postId){
        logger.info("Updating post comment reply count");
        postInfoRepository.findById(postId).map(postInfo -> {
            postInfo.setCommentReply(postInfo.getCommentReply()+1);
            return postInfoRepository.save(postInfo);
        });

    }
}


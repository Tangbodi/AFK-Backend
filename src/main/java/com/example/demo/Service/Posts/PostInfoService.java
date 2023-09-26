package com.example.demo.Service.Posts;

import com.example.demo.Model.DTO.GameGenreMapIdDTO;
import com.example.demo.Model.DTO.ObjectUserDTO;
import com.example.demo.Model.DTO.PostDTO;
import com.example.demo.Model.Entity.PostsInfo;
import com.example.demo.Model.Entity.UsersFavoritePost;
import com.example.demo.Model.VO.PopularPostVO;
import com.example.demo.Model.VO.PostInfoVO;
import com.example.demo.Mapper.Repository.PostGameMapRepository;
import com.example.demo.Mapper.Repository.PostInfoRepository;
import com.example.demo.Mapper.Repository.UserFavoritePostRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Service
public class PostInfoService {
    private static final Logger logger = LoggerFactory.getLogger(PostInfoService.class);

    @Autowired
    private PostInfoRepository postInfoRepository;
    @Autowired
    private PostGameMapRepository postGameMapRepository;
    @Autowired
    private UserFavoritePostRepository userFavoritePostRepository;
    private ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

    @Async("MultiExecutor")
    @Transactional
    public void SetPostInfo(PostDTO postDTO) {
        logger.info("Setting post info: {}");
        try {
            PostsInfo postsInfo = new PostsInfo();
            postsInfo.setId(postDTO.getPostId());
            postsInfo.setView(0);
            postsInfo.setCommentReply(0);
            postsInfo.setLike(0);
            postsInfo.setSave(0);
            postInfoRepository.save(postsInfo); // This will automatically be transactional
            logger.info("Post info saved successfully: {}");
        } catch (Exception e) {
            logger.error("Failed to set post info: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to set post info " +e); // Rethrow the exception to trigger rollback
        }
    }

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
                popularPostVO.setGenreId(Byte.valueOf(popularPost.get("genre_id").toString()));
                popularPostVO.setGameId(Short.valueOf(popularPost.get("game_id").toString()));
                popularPostVO.setPostId(popularPost.get("post_id").toString());
                popularPostVO.setTitle(String.valueOf( popularPost.get("title")));
                popularPostVO.setGameName(String.valueOf(popularPost.get("game_name")));
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
                postInfoVO.setTitle(String.valueOf( map.get("title")));
                postInfoVO.setView((Integer) map.get("view"));
                postInfoVO.setComment((Integer) map.get("comment"));
                postInfoVO.setLike((Integer) map.get("like"));
                postInfoVO.setSave((Integer) map.get("save"));
                postInfoVO.setUsername(String.valueOf( map.get("username")));
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
    public void CalculatePostTotalLike(List<Long> postIds){
        logger.info("Finding all users favorite post list with like status = 1");
        //transverse all users favorite post list
        for(Long postId : postIds){
            Map<String,Object> map = userFavoritePostRepository.findPostTotalLikeByLikeStatus(postId);
            Integer totalLike = ((BigInteger) map.get("total_like")).intValue();
            logger.info("Total like: {}",totalLike);
            //Update post like count
            UpdatePostLikeCount(postId,totalLike);
            logger.info("Updated post total like count");
        }
    }
    private void UpdatePostLikeCount(Long postId, Integer totalLike) {
        logger.info("Updating post like count");
        postInfoRepository.findById(postId).map(postInfo -> {
            postInfo.setLike(totalLike);
            return postInfoRepository.save(postInfo);
        }).orElseThrow(()-> new RuntimeException("Failed to update post like count"));
    }
    public void CalculatePostTotalSave(List<Long> postIds){
        logger.info("Finding all users favorite post list with save status = 1");
        for(Long postId: postIds){
            Map<String,Object> map = userFavoritePostRepository.findPostTotalLikeBySaveStatus(postId);
            Integer totalSave = ((BigInteger) map.get("total_save")).intValue();
            logger.info("Total save: {}",totalSave);
            //Update post save count
            UpdatePostSaveCount(postId,totalSave);
            logger.info("Updated post total save count");
        }
    }
    private void UpdatePostSaveCount(Long postId, Integer totalSave) {
        logger.info("Updating post save count");
        postInfoRepository.findById(postId).map(postInfo -> {
            postInfo.setSave(totalSave);
            return postInfoRepository.save(postInfo);
        });
    }

    public void UpdatePostViewCount(Long postId) {
        logger.info("Updating post view count");
        postInfoRepository.findById(postId).map(postInfo -> {
            postInfo.setView(postInfo.getView() + 1);
            return postInfoRepository.save(postInfo);
        }).orElseThrow(() -> new RuntimeException("Failed to update post view count"));
    }

    public void UpdatePostCommentReplyCount(Long postId, Integer total){
        logger.info("Updating post comment reply count");
        postInfoRepository.findById(postId).map(postInfo -> {
            postInfo.setCommentReply(postInfo.getCommentReply()+total);
            return postInfoRepository.save(postInfo);
        });
    }
}


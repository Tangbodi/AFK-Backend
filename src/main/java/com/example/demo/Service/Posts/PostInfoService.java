package com.example.demo.Service.Posts;

import com.example.demo.Mapper.Repository.PostsInfoRepository;
import com.example.demo.Model.DTO.ReplyDTO;
import com.example.demo.Model.VO.PopularPostVO;
import com.example.demo.Model.VO.ReplyVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class PostInfoService {
    private static final Logger logger = LoggerFactory.getLogger(PostInfoService.class);

    @Autowired
    private PostsInfoRepository postsInfoRepository;
    public List<PopularPostVO> GetMostPopularPosts() {
        logger.info("Getting most popular posts: {}");
        try {
            List<Map<Short, Object>> popularPosts = postsInfoRepository.findMostPopularPosts();
            if (popularPosts != null) {
                logger.info("Got most popular posts: {}");
                List<PopularPostVO> popularPostVOList = TransferToPopularPostVO(popularPosts);
                return popularPostVOList;
            } else {
                logger.info("No popular posts found: {}");
                return null;
            }
        } catch (Exception e) {
            logger.error("Failed to get most popular posts: {}", e.getMessage(), e);
        }
        return null;
    }
        public List<PopularPostVO> TransferToPopularPostVO(List<Map<Short, Object>> popularPosts) {
        logger.info("Transferring popular posts to VO: {}");
        try{
            List<PopularPostVO> popularPostVOList = new ArrayList<>();
            for(Map<Short, Object> popularPost : popularPosts){
                PopularPostVO popularPostVO = new PopularPostVO();
                popularPostVO.setPostId((String) popularPost.get("post_id"));
                popularPostVO.setTitle((String) popularPost.get("title"));
                popularPostVO.setGameName((String) popularPost.get("game_name"));
                popularPostVOList.add(popularPostVO);
            }
            return popularPostVOList;
        }catch (Exception e){
            logger.error("Failed to transfer popular posts to VO: {}",e.getMessage(),e);
        }
        return null;
    }
}

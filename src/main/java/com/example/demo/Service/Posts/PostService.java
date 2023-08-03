package com.example.demo.Service.Posts;

import com.example.demo.Mapper.Repository.PostsGenresRepository;
import com.example.demo.Mapper.Repository.PostsInfoRepository;
import com.example.demo.Mapper.Repository.PostsRepository;
import com.example.demo.Mapper.Repository.PostsUsersMapRepository;
import com.example.demo.Model.DTO.PostDTO;
import com.example.demo.Model.Entity.*;
import com.example.demo.Model.VO.PostVO;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.UUID;

@Service
public class PostService {
    private static final Logger logger = LoggerFactory.getLogger(PostService.class);

    @Autowired
    private PostsRepository postsRepository;
    @Autowired
    private PostsInfoRepository postsInfoRepository;
    @Autowired
    private PostsUsersMapRepository postsUsersMapRepository;
    @Autowired
    private PostsGenresRepository postsGenresRepository;

    @Transactional
    public PostVO EditPost(PostDTO postDTO) {
        logger.info("Setting post for userId: {}" + postDTO.getUserId());
        try {
            Document document = Jsoup.parse(postDTO.getTextRender());
            String textHTML = document.html();
            String uuid = UUID.randomUUID().toString();
            postDTO.setPostId(uuid);
            Post post = new Post();
            post.setId(uuid);
            post.setTitle(postDTO.getTitle());
//            int start = textHTML.indexOf("<body>") + 6;
//            int end = textHTML.indexOf("</body>");
//            textHTML = textHTML.substring(start, end);
            logger.info("Removed body tag from textHTML: {}"+textHTML);
            post.setTextRender(textHTML);
            post.setIpvFour(postDTO.getIpvFour());
            post.setIpvSix(postDTO.getIpvSix());
            post.setCreatedAt(postDTO.getCreatedAt());
            post.setModifiedAt(postDTO.getCreatedAt());
            if (postsRepository.save(post) != null) {
                logger.info("Post saved successfully: {}");
                SetPostInfo(postDTO);
                SetPostUserMap(postDTO);
                SetPostGenreMap(postDTO);
            } else {
                logger.info("Failed to save post: {}");
            }
            PostVO postVO = new PostVO();
            postVO.setUserId(postDTO.getUserId());
            postVO.setPostId(postDTO.getPostId());
            postVO.setGenreId(postDTO.getGenreId());
            postVO.setCreatedAt(postDTO.getCreatedAt());
            return postVO;
        } catch (Exception e) {
            logger.error("Failed to set post: {}", e);
        }
        return null;
    }

    @Transactional
    public void SetPostInfo(PostDTO postDTO) {
        logger.info("Setting post info: {}");
        try {
            PostsInfo postsInfo = new PostsInfo();
            postsInfo.setPostId(postDTO.getPostId());
            postsInfo.setView(0);
            postsInfo.setComment(0);
            postsInfo.setLike(0);
            postsInfo.setFavorite(0);
            if (postsInfoRepository.save(postsInfo) != null) {
                logger.info("Post info saved successfully: {}");
            } else {
                logger.info("Failed to save post info: {}");
            }
        } catch (Exception e) {
            logger.error("Failed to set post info: {}", e.getMessage(),e);
        }
    }

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
            if (postsUsersMapRepository.save(postsUsersMap) != null) {
                logger.info("Post info saved successfully: {}");
            } else {
                logger.info("Failed to save post info: {}");
            }
        } catch (Exception e) {
            logger.error("Failed to set post info: {}", e.getMessage(),e);
        }
    }
    @Transactional
    public void SetPostGenreMap(PostDTO postDTO) {
        logger.info("Setting post genre map: {}");
        try {
            PostsGenresMapId postsGenresMapId = new PostsGenresMapId();
            PostsGenresMap postsGenresMap = new PostsGenresMap();
            postsGenresMapId.setPostId(postDTO.getPostId());
            postsGenresMapId.setGenreId(postDTO.getGenreId());
            postsGenresMap.setId(postsGenresMapId);
            postsGenresMap.setCreatedAt(postDTO.getCreatedAt());
            postsGenresMap.setModifiedAt(postDTO.getCreatedAt());
            if(postsGenresRepository.save(postsGenresMap)!=null){
                logger.info("Post genre map saved successfully: {}");
            }else{
                logger.info("Failed to save post genre map: {}");
            }
        } catch (Exception e) {
            logger.error("Failed to set post genre map: {}", e.getMessage(),e);
        }
    }
}

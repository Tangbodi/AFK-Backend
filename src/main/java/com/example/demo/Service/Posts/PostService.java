package com.example.demo.Service.Posts;

import com.example.demo.Mapper.Repository.*;
import com.example.demo.Model.DTO.PostDTO;
import com.example.demo.Model.Entity.Post;
import com.example.demo.Model.Entity.PostsInfo;
import com.example.demo.Model.Entity.PostsUsersMap;
import com.example.demo.Model.Entity.PostsUsersMapId;
import com.example.demo.Model.VO.PostSavedVO;
import com.example.demo.Model.VO.ShowPostVO;
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
    private PostRepository postRepository;
    @Autowired
    private PostsInfoRepository postsInfoRepository;
    @Autowired
    private PostsUsersMapRepository postsUsersMapRepository;
    @Autowired
    private PostsGenresRepository postsGenresRepository;
    @Autowired
    private UsersInfoRepository usersInfoRepository;
    @Transactional
    public PostSavedVO EditPost(PostDTO postDTO) {
        logger.info("Setting post for userId: {}" + postDTO.getUserId());
        try {
            Document document = Jsoup.parse(postDTO.getTextRender());
            String textHTML = document.html();
            String uuid = UUID.randomUUID().toString();
            postDTO.setPostId(uuid);
            Post post = new Post();
            post.setId(uuid);
            post.setTitle(postDTO.getTitle());
            int start = textHTML.indexOf("<body>") + 6;
            int end = textHTML.indexOf("</body>");
            textHTML = textHTML.substring(start, end);
            logger.info("Removed body tag from textHTML: {}" + textHTML);
            post.setTextRender(textHTML);
            post.setIpvFour(postDTO.getIpvFour());
            post.setIpvSix(postDTO.getIpvSix());
            post.setCreatedAt(postDTO.getCreatedAt());
            post.setModifiedAt(postDTO.getCreatedAt());
            if (postRepository.save(post) != null) {
                logger.info("Post saved successfully: {}");
                SetPostInfo(postDTO);
                SetPostUserMap(postDTO);
//                SetPostGenreMap(postDTO);
            } else {
                logger.info("Failed to save post: {}");
            }
            PostSavedVO postSavedVO = TransferToPostSavedVO(postDTO);
            return postSavedVO;
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
            logger.error("Failed to set post info: {}", e.getMessage(), e);
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
            logger.error("Failed to set post info: {}", e.getMessage(), e);
        }
    }

    public PostSavedVO TransferToPostSavedVO(PostDTO postDTO) {
        PostSavedVO postSavedVO = new PostSavedVO();
        postSavedVO.setPostId(postDTO.getPostId());
        postSavedVO.setCreatedAt(postDTO.getCreatedAt());
        return postSavedVO;
    }


    public ShowPostVO GetPost(String postId) {
        logger.info("Getting post: {}");
        try {
            Post post = postRepository.findById(postId).orElse(null);
            if (post != null) {
                logger.info("Post found: {}");
                ShowPostVO showPostVO = TransferToShowPostVO(post);
                return showPostVO;
            } else {
                logger.info("Post not found: {}");
            }
        } catch (Exception e) {
            logger.error("Failed to get post: {}", e.getMessage(), e);
        }
        return null;
    }
    public ShowPostVO TransferToShowPostVO(Post post) {
        logger.info("Transferring post to VO: {}");
        try{
            ShowPostVO showPostVO = new ShowPostVO();
            String postId = post.getId();
            PostsUsersMap postsUsersMap = postsUsersMapRepository.findByPostId(postId).orElse(null);
            if(postsUsersMap!=null){
                String userId = postsUsersMap.getId().getUserId();
                String username = usersInfoRepository.findById(userId).orElse(null).getUsername();
                showPostVO.setUserName(username);
                showPostVO.setTitle(post.getTitle());
                showPostVO.setTextRender(post.getTextRender());
                showPostVO.setCreatedAt(post.getCreatedAt());
//            showPostVO.setImageURL();
                return showPostVO;
            } else {
                logger.info("Post not found: {}");
            }
        } catch (Exception e) {
            logger.error("Failed to transfer post to VO: {}", e.getMessage(), e);
        }

        return null;
    }
}

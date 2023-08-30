package com.example.demo.Service.Posts;

import com.example.demo.Model.DTO.GetPostDTO;
import com.example.demo.Model.DTO.PostDTO;
import com.example.demo.Model.Entity.Post;
import com.example.demo.Model.VO.PostSavedVO;
import com.example.demo.Model.VO.SearchPostVO;
import com.example.demo.Model.VO.ShowPostBodyVO;
import com.example.demo.Repository.PostGameMapRepository;
import com.example.demo.Repository.PostRepository;
import com.example.demo.Service.IP.IpAddressService;
import com.example.demo.Util.Snowflake;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class PostService {
    private static final Logger logger = LoggerFactory.getLogger(PostService.class);

    @Autowired
    private PostRepository postRepository;
    @Autowired
    private PostGameMapRepository postGameMapRepository;
    @Autowired
    private PostGameMapService postGameMapService;
    @Autowired
    private PostInfoService postInfoService;
    @Autowired
    private PostImageService postImageService;
    @Autowired
    private IpAddressService ipAddressService;
    @Autowired
    private PostUserMapService postUserMapService;

    @Transactional(rollbackOn = Exception.class)
    public PostSavedVO SavePost(PostDTO postDTO) throws Exception {
        logger.info("Saving post");
        try {
//            PostDTO postDTO = redisPostService.GetPostDTOViaCache(userId);
            if (postDTO != null) {
                logger.info("Transfer postDTO to post");
                postDTO.setCreatedAt(Instant.now());
                Post post = new Post();
                long postId = Snowflake.generateUniqueId();
                postDTO.setPostId(postId);
                post.setId(postDTO.getPostId());
                post.setTitle(postDTO.getTitle());
                Document document = Jsoup.parse(postDTO.getTextRender());
                String textHTML = document.html();
                int start = textHTML.indexOf("<body>") + 6;
                int end = textHTML.indexOf("</body>");
                textHTML = textHTML.substring(start, end);
                logger.info("Removed body tag from textHTML: {}" + textHTML);
                post.setTextRender(textHTML);
                post.setCreatedAt(postDTO.getCreatedAt());
                post.setModifiedAt(postDTO.getCreatedAt());
                postRepository.save(post);
                logger.info("Post saved successfully");
                if (!postDTO.getPostImageNameList().isEmpty()) {
                    postImageService.SavePostImage(postDTO);
                } else {
                    logger.info("No image found in postDTO");
                }
                ipAddressService.SetPostIpAddress(postDTO);
                postInfoService.SetPostInfo(postDTO);
                postUserMapService.SetPostUserMap(postDTO);
                postGameMapService.SetPostGameMap(postDTO);
            } else {
                return null;
            }
//            redisPostService.DeletePostCache(postDTO.getUserId());
            return TransferToPostSavedVO(postDTO);
        } catch (Exception e) {
            logger.error("Failed to save post: {}", e.getMessage(), e);
            throw new Exception("Failed to save post " + e); // Rethrow the exception to trigger rollback
        }
    }


    private PostSavedVO TransferToPostSavedVO(PostDTO postDTO) {
        logger.info("Transferring post to VO for post ID: {}", postDTO.getPostId());
        try {
            PostSavedVO postSavedVO = new PostSavedVO();
            postSavedVO.setPostId(postDTO.getPostId().toString());
            postSavedVO.setCreatedAt(postDTO.getCreatedAt());
            return postSavedVO;
        } catch (Exception e) {
            logger.error("Failed to transfer post to VO: {}", e.getMessage(), e);
        }
        return null;
    }

    public ShowPostBodyVO GetPost(GetPostDTO getPostDTO) {
        logger.info("Getting post for post ID: {}", getPostDTO.getPostId());

        try {
            List<Map<String, Object>> post = postGameMapRepository.findByGenreGamePostId(getPostDTO.getGenreId(), getPostDTO.getGameId(), getPostDTO.getPostId());

            if (post.isEmpty()) {
                logger.info("Post not found: " + getPostDTO.getPostId());
                return null;
            } else {
                logger.info("Post found: " + getPostDTO.getPostId());
                List<Map<String, Object>> postImageList = postImageService.findAllImageURLsByPostId(getPostDTO);
                // Update post viewed count
                postInfoService.UpdatePostViewCount(getPostDTO.getPostId());
                return TransferToShowPostVO(post, postImageList);
            }
        } catch (Exception e) {
            logger.error("Failed to get post: {}", e.getMessage(), e);
            return null;
        }
    }

    private ShowPostBodyVO TransferToShowPostVO(List<Map<String, Object>> post, List<Map<String, Object>> postImageList) {
        logger.info("Transferring post to VO for post ID: {}");
        try {
            ShowPostBodyVO showPostBodyVO = new ShowPostBodyVO();
            for (Map<String, Object> map : post) {
                showPostBodyVO.setPostId(map.get("post_id").toString());
                logger.info("Post ID: {}", map.get("post_id"));
                showPostBodyVO.setUserId(map.get("user_id").toString());
                logger.info("User ID: {}", map.get("user_id"));
                showPostBodyVO.setUserName((String) map.get("username"));
                showPostBodyVO.setTitle((String) map.get("title"));
                showPostBodyVO.setTextRender((String) map.get("text_render"));
                showPostBodyVO.setView((Integer) map.get("view"));
                showPostBodyVO.setCommentReply((Integer) map.get("comment_reply"));
                showPostBodyVO.setLike((Integer) map.get("like"));
                showPostBodyVO.setSave((Integer) map.get("save"));
                showPostBodyVO.setLikeStatus((Byte) map.get("like_status"));
                showPostBodyVO.setSaveStatus((Byte) map.get("save_status"));
                Timestamp timestamp = (Timestamp) map.get("created_at");
                showPostBodyVO.setCreatedAt(timestamp.toInstant());
            }
            List<String> ImageURLList = new ArrayList<>();
            for (Map<String, Object> map : postImageList) {
                ImageURLList.add((String) map.get("image_url"));
            }
            showPostBodyVO.setImageURL(ImageURLList);
            logger.info("Transferred post to VO successfully for post ID: {}", showPostBodyVO.getPostId());
            return showPostBodyVO;
        } catch (Exception e) {
            logger.error("Failed to transfer post to VO: {}", e.getMessage(), e);
        }
        return null;
    }


    public List<SearchPostVO> SearchByKeyword(String keyword) {
        logger.info("Searching by keyword: {}", keyword);
        try {
            List<Post> postList = postRepository.findByKeyword(keyword);
            if (postList != null) {
                logger.info("Content found related to keyword: {}", keyword);
                List<SearchPostVO> searchPostVOList = new ArrayList<>();
                for (Post post : postList) {
                    SearchPostVO searchPostVO = new SearchPostVO();
                    searchPostVO.setPostId(post.getId().toString());
                    searchPostVO.setTitle(post.getTitle());
                    searchPostVO.setTextRender(post.getTextRender());
                    searchPostVOList.add(searchPostVO);
                }
                return searchPostVOList;
            } else {
                logger.info("No content related to keyword: {}", keyword);
                return Collections.emptyList(); // Return an empty list instead of null
            }
        } catch (Exception e) {
            logger.error("Failed to search by keyword: {}", e.getMessage(), e);
            return Collections.emptyList(); // Return an empty list instead of null
        }
    }

}

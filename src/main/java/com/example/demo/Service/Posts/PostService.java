package com.example.demo.Service.Posts;

import com.example.demo.Exception.PostNotFoundException;
import com.example.demo.Exception.UserNotFoundException;
import com.example.demo.Mapper.Repository.*;
import com.example.demo.Model.DTO.GetPostDTO;
import com.example.demo.Model.DTO.IpAddressDTO;
import com.example.demo.Model.DTO.PostDTO;
import com.example.demo.Model.Entity.*;
import com.example.demo.Model.VO.PostInfoVO;
import com.example.demo.Model.VO.PostSavedVO;
import com.example.demo.Model.VO.SearchPostVO;
import com.example.demo.Model.VO.ShowPostBodyVO;
import com.example.demo.Service.IP.IpAddressService;
import com.example.demo.Service.IP.IpService;
import com.example.demo.Service.Redis.RedisPostService;
import com.example.demo.Util.Snowflake;
import com.example.demo.Util.UUIDCreator;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.math.BigInteger;
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
    private PostsInfoRepository postsInfoRepository;
    @Autowired
    private PostUserMapRepository postUserMapRepository;
    @Autowired
    private PostGameMapRepository postGameMapRepository;
    @Autowired
    private UserInfoRepository userInfoRepository;
    @Autowired
    private RedisPostService redisPostService;
    @Autowired
    private PostImageService postImageService;
    @Autowired
    private IpAddressService ipAddressService;

    public void SetPostCache(PostDTO postDTO) {
        logger.info("Setting post for userId: {}" + postDTO.getUserId());
        try {
            long uuid = Snowflake.generateUniqueId();
            postDTO.setPostId(uuid);
            redisPostService.SetPostCache(postDTO);
        } catch (Exception e) {
            logger.error("Failed to set post: {}", e.getMessage(), e);
        }
    }


    public PostSavedVO SavePost(Long userId, List<MultipartFile> imageFiles) {
        logger.info("Saving post");
        try {
            PostDTO postDTO = redisPostService.GetPostDTOViaCache(userId);
            if (postDTO != null) {
                logger.info("Transfer postDTO to post");
                postDTO.setCreatedAt(Instant.now());
                Post post = new Post();
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
                if (postRepository.save(post) != null) {
                    logger.info("Post saved successfully");
                    postImageService.SavePostImage(imageFiles, postDTO);
                    ipAddressService.SetPostIpAddress(postDTO);
                    SetPostInfo(postDTO);
                    SetPostUserMap(postDTO);
                    SetPostGameMap(postDTO);
                } else {
                    logger.info("Failed to save post");
                    return null;
                }
            } else {
                return null;
            }
            redisPostService.DeletePostCache(postDTO.getUserId());
            return TransferToPostSavedVO(postDTO);
        } catch (Exception e) {
            logger.error("Failed to save post: {}", e.getMessage(), e);
        }
        return null;
    }

    @Async("MultiExecutor")
    @Transactional
    public void SetPostInfo(PostDTO postDTO) {
        logger.info("Setting post info: {}");
        try {
            PostsInfo postsInfo = new PostsInfo();
            postsInfo.setId(postDTO.getPostId());
            postsInfo.setView(0);
            postsInfo.setComment(0);
            postsInfo.setLike(0);
            postsInfo.setSave(0);
            if (postsInfoRepository.save(postsInfo) != null) {
                logger.info("Post info saved successfully: {}");
            } else {
                logger.info("Failed to save post info: {}");
            }
        } catch (Exception e) {
            logger.error("Failed to set post info: {}", e.getMessage(), e);
        }
    }
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
        }
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
        }
    }
    private PostSavedVO TransferToPostSavedVO(PostDTO postDTO) {
        logger.info("Transferring post to VO for post ID: {}", postDTO.getPostId());
        try {
            PostSavedVO postSavedVO = new PostSavedVO();
            postSavedVO.setPostId(postDTO.getPostId());
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
            List<Map<Short,Object>> post = postGameMapRepository.findByGenreGamePostId(getPostDTO.getGenreId(),getPostDTO.getGameId(),getPostDTO.getPostId());
            if (post.isEmpty()) {
                logger.info("Post not found: " + getPostDTO.getPostId());
                return null;
            } else {
                logger.info("Post found: " + getPostDTO.getPostId());
                List<Map<Short,Object>> postImageList = postImageService.findAllImageURLByPostId(getPostDTO);
                return TransferToShowPostVO(post,postImageList);
            }
        } catch (PostNotFoundException e) {
            logger.error("Failed to get post: {}", e.getMessage(), e);
            throw e; // Re-throw the custom exception to be handled at the controller level
        } catch (Exception e) {
            logger.error("Failed to get post: {}", e.getMessage(), e);
            return null;
        }
    }

    private ShowPostBodyVO TransferToShowPostVO(List<Map<Short,Object>> post, List<Map<Short,Object>> postImageList) {
        logger.info("Transferring post to VO for post ID: {}");
        try {
            ShowPostBodyVO showPostBodyVO = new ShowPostBodyVO();
            for(Map<Short,Object> map : post){
                showPostBodyVO.setPostId(((BigInteger) map.get("post_id")).longValue());
                logger.info("Post ID: {}", showPostBodyVO.getPostId());
                showPostBodyVO.setUserId(((BigInteger) map.get("user_id")).longValue());
                logger.info("User ID: {}", showPostBodyVO.getUserId());
                showPostBodyVO.setUserName((String) map.get("username"));
                showPostBodyVO.setTitle((String) map.get("title"));
                showPostBodyVO.setTextRender((String) map.get("text_render"));
                Timestamp timestamp = (Timestamp) map.get("created_at");
                showPostBodyVO.setCreatedAt(timestamp.toInstant());
            }
            List<String> ImageURLList = new ArrayList<>();
            for(Map<Short,Object> map : postImageList){
                ImageURLList.add((String) map.get("image_url"));
            }
            showPostBodyVO.setImageURL(ImageURLList);
            logger.info("Transferred post to VO successfully for post ID: {}", showPostBodyVO.getPostId());
            return showPostBodyVO;
            }catch (Exception e){
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
                    searchPostVO.setPostId(post.getId());
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
                postHistoryVO.setPostId(((BigInteger) map.get("post_id")).longValue());
                postHistoryVO.setTitle((String) map.get("title"));
                postHistoryVO.setUsername((String) map.get("username"));
                postHistoryVO.setView((Integer) map.get("view"));
                postHistoryVO.setComment((Integer) map.get("comment"));
                postHistoryVO.setLike((Integer) map.get("like"));
                postHistoryVO.setSave((Integer) map.get("save"));
                Timestamp timestamp = (Timestamp) map.get("created_at");
                postHistoryVO.setCreatedAt(timestamp.toInstant());
                postHistoryVOList.add(postHistoryVO);
            }
        } catch (Exception e) {
            logger.error("Failed to transfer all posts by user ID to VO", e);
            return Collections.emptyList();
        }
        return postHistoryVOList;
    }
}

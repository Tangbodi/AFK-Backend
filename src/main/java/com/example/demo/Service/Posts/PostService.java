package com.example.demo.Service.Posts;

import com.example.demo.Exception.PostNotFoundException;
import com.example.demo.Exception.UserNotFoundException;
import com.example.demo.Mapper.Repository.*;
import com.example.demo.Model.DTO.GetPostDTO;
import com.example.demo.Model.DTO.PostDTO;
import com.example.demo.Model.Entity.*;
import com.example.demo.Model.VO.PostHistoryVO;
import com.example.demo.Model.VO.PostSavedVO;
import com.example.demo.Model.VO.SearchPostVO;
import com.example.demo.Model.VO.ShowPostVO;
import com.example.demo.Service.Redis.RedisPostService;
import com.example.demo.Util.UUIDCreator;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.util.*;

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
    private PostsGamesMapRepository postsGamesMapRepository;
    @Autowired
    private UsersInfoRepository usersInfoRepository;
    @Autowired
    private RedisPostService redisPostService;
    @Autowired
    private PostImageService postImageService;

    public void SetPostCache(PostDTO postDTO) {
        logger.info("Setting post for userId: {}" + postDTO.getUserId());
        try {
            Document document = Jsoup.parse(postDTO.getTextRender());
            String textHTML = document.html();
            String uuid = UUIDCreator.CreateUUID();
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
            redisPostService.SetPostCache(post, postDTO.getUserId());
//            if (postRepository.save(post) != null) {
//                logger.info("Post saved successfully: {}");
//                SetPostInfo(postDTO);
//                SetPostUserMap(postDTO);
//                SetPostGameMap(postDTO);
//            } else {
//                logger.info("Failed to save post: {}");
//            }
//            PostSavedVO postSavedVO = TransferToPostSavedVO(postDTO);
//            return postSavedVO;
        } catch (Exception e) {
            logger.error("Failed to set post: {}", e.getMessage(), e);
        }
    }

    @Transactional
    public PostSavedVO SavePost(String userId) {
        logger.info("Saving post");
        try {
            Post post = redisPostService.GetPostViaCache(userId);
            postRepository.save(post);
            SetPostInfo(post);
            SetPostUserMap(post, userId);
            redisPostService.DeletePostCache(userId);
            return TransferToPostSavedVO(post);
        } catch (Exception e) {
            logger.error("Failed to save post: {}", e.getMessage(), e);
        }
        return null;
    }

    @Transactional
    public void SetPostInfo(Post post) {
        logger.info("Setting post info: {}");
        try {
            PostsInfo postsInfo = new PostsInfo();
            postsInfo.setPostId(post.getId());
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
    public void SetPostUserMap(Post post, String userId) {
        logger.info("Setting post user map: {}");
        try {
            PostsUsersMapId postsUsersMapId = new PostsUsersMapId();
            PostsUsersMap postsUsersMap = new PostsUsersMap();
            postsUsersMapId.setPostId(post.getId());
            postsUsersMapId.setUserId(userId);
            postsUsersMap.setId(postsUsersMapId);
            postsUsersMap.setCreatedAt(post.getCreatedAt());
            postsUsersMap.setModifiedAt(post.getCreatedAt());
            if (postsUsersMapRepository.save(postsUsersMap) != null) {
                logger.info("Post info saved successfully: {}");
            } else {
                logger.info("Failed to save post info: {}");
            }
        } catch (Exception e) {
            logger.error("Failed to set post info: {}", e.getMessage(), e);
        }
    }

    private PostSavedVO TransferToPostSavedVO(Post post) {
        logger.info("Transferring post to VO for post ID: {}", post.getId());
        try {
            PostSavedVO postSavedVO = new PostSavedVO();
            postSavedVO.setPostId(post.getId());
            postSavedVO.setCreatedAt(post.getCreatedAt());
            return postSavedVO;
        } catch (Exception e) {
            logger.error("Failed to transfer post to VO: {}", e.getMessage(), e);
        }
        return null;
    }

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
            if (postsGamesMapRepository.save(postsGamesMap) != null) {
                logger.info("Post game map saved successfully: {}");
            } else {
                logger.info("Failed to save post game map: {}");
            }
        } catch (Exception e) {
            logger.error("Failed to set post game map: {}", e.getMessage(), e);
        }
    }


    public ShowPostVO GetPost(GetPostDTO getPostDTO) {
        logger.info("Getting post for post ID: {}", getPostDTO.getPostId());

        try {
            PostsGamesMap postsGamesMap = postsGamesMapRepository.findById(getPostDTO.getPostId()).orElse(null);
            Post post = postRepository.findById(getPostDTO.getPostId()).orElse(null);
            if (postsGamesMap == null || post == null) {
                logger.info("Post not found: " + getPostDTO.getPostId());
                return null;
            } else if (postsGamesMap.getId().equals(getPostDTO.getPostId()) &&
                    postsGamesMap.getGameId().equals(getPostDTO.getGameId()) &&
                    postsGamesMap.getGenreId().equals(getPostDTO.getGenreId())) {
                return TransferToShowPostVO(post);
            } else {
                logger.info("PostId, GenreId, and GameId doesn't match with ID: " + getPostDTO.getPostId());
                return null;
            }
        } catch (PostNotFoundException e) {
            logger.error("Failed to get post: {}", e.getMessage(), e);
            throw e; // Re-throw the custom exception to be handled at the controller level
        } catch (Exception e) {
            logger.error("Failed to get post: {}", e.getMessage(), e);
            return null;
        }
    }

    private ShowPostVO TransferToShowPostVO(Post post) {
        logger.info("Transferring post to VO for post ID: {}", post.getId());

        try {
            ShowPostVO showPostVO = new ShowPostVO();
            String postId = post.getId();
            PostsUsersMap postsUsersMap = postsUsersMapRepository.findByPostId(postId)
                    .orElseThrow(() -> new PostNotFoundException("Post not found with ID: " + postId));

            String userId = postsUsersMap.getId().getUserId();
            UsersInfo userInfo = usersInfoRepository.findById(userId)
                    .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

            showPostVO.setUserName(userInfo.getUsername());
            showPostVO.setTitle(post.getTitle());
            showPostVO.setTextRender(post.getTextRender());
            showPostVO.setCreatedAt(post.getCreatedAt());

            logger.info("Transferred post to VO successfully for post ID: {}", post.getId());
            return showPostVO;
        } catch (PostNotFoundException | UserNotFoundException e) {
            logger.error("Failed to transfer post to VO: {}", e.getMessage(), e);
            throw e; // Re-throw the custom exceptions to be handled at the controller level
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

    public List<PostHistoryVO> GetAllPostsByUserId(String userId) {
        logger.info("Getting all posts by user ID");
        try {
            List<Map<Short, Object>> allPostsByUserId = postsUsersMapRepository.findAllPostsByUserId(userId);
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

    private static List<PostHistoryVO> TransferToPostHistoryVO(List<Map<Short, Object>> allPostsByUserId) {
        logger.info("Transferring all posts by user ID to VO");
        List<PostHistoryVO> postHistoryVOList = new ArrayList<>();
        try {
            for (Map<Short, Object> map : allPostsByUserId) {
                PostHistoryVO postHistoryVO = new PostHistoryVO();
                postHistoryVO.setPostId((String) map.get("post_id"));
                postHistoryVO.setTitle((String) map.get("title"));
                postHistoryVO.setView((Integer) map.get("view"));
                postHistoryVO.setComment((Integer) map.get("comment"));
                postHistoryVO.setLike((Integer) map.get("like"));
                postHistoryVO.setFavorite((Integer) map.get("favorite"));
                Timestamp timestamp = (Timestamp) map.get("created_at");
                postHistoryVO.setCreated_at(timestamp.toInstant());
                postHistoryVOList.add(postHistoryVO);
            }
        } catch (Exception e) {
            logger.error("Failed to transfer all posts by user ID to VO", e);
            return Collections.emptyList();
        }
        return postHistoryVOList;
    }
}

package com.example.demo.Service.Posts;

import com.example.demo.Exception.PostNotFoundException;
import com.example.demo.Exception.UserNotFoundException;
import com.example.demo.Mapper.Repository.*;
import com.example.demo.Model.DTO.GetPostDTO;
import com.example.demo.Model.DTO.PostDTO;
import com.example.demo.Model.Entity.*;
import com.example.demo.Model.VO.PostSavedVO;
import com.example.demo.Model.VO.SearchPostVO;
import com.example.demo.Model.VO.ShowPostVO;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
    private PostsGamesMapRepository postsGamesMapRepository;
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
                SetPostGameMap(postDTO);
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

}

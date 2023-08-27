package com.example.demo.Service.Comments;

import com.example.demo.Repository.CommentRepository;
import com.example.demo.Model.DTO.CommentReplyDTO;
import com.example.demo.Model.Entity.PostComment;
import com.example.demo.Model.VO.CommentSavedVO;
import com.example.demo.Model.VO.NewestCommentVO;
import com.example.demo.Service.IP.IpAddressService;
import com.example.demo.Service.Posts.PostInfoService;
import com.example.demo.Service.Replies.ReplyService;
import com.example.demo.Util.Snowflake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;

@Service
public class CommentService {
    private static final Logger logger = LoggerFactory.getLogger(CommentService.class);
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private ReplyService replyService;
    @Autowired
    private IpAddressService ipAddressService;
    @Autowired
    private PostInfoService postInfoService;

    @Transactional
    public CommentSavedVO SetComment(CommentReplyDTO commentReplyDTO) {
        logger.info("Setting comment: {}");
        try {
            long commentId = Snowflake.generateUniqueId();
            commentReplyDTO.setCommentId(commentId);
            commentReplyDTO.setCreatedAt(Instant.now());
            PostComment postComment = new PostComment();
            postComment.setId(commentId);
            postComment.setContent(commentReplyDTO.getContent());
            postComment.setFromUid(commentReplyDTO.getFromUid());
            postComment.setPostId(commentReplyDTO.getPostId());
            postComment.setCreatedAt(commentReplyDTO.getCreatedAt());
            postComment.setModifiedAt(commentReplyDTO.getCreatedAt());
            PostComment savedComment = commentRepository.save(postComment);
            if (savedComment != null) {
                logger.info("Comment saved successfully: {}", savedComment);
                ipAddressService.SetCommentReplyIpAddress(commentReplyDTO);
                //update post comment reply count
                postInfoService.UpdatePostCommentReplyCount(commentReplyDTO.getPostId());
                return TransferToVO(commentReplyDTO);
            } else {
                logger.info("Comment not saved: {}");
            }
        } catch (Exception e) {
            logger.error("Failed to set comment: {}", e.getMessage(), e);
        }
        return null;
    }

    private static CommentSavedVO TransferToVO(CommentReplyDTO commentReplyDTO) {
        CommentSavedVO commentSavedVO = new CommentSavedVO();
        commentSavedVO.setCommentId(commentReplyDTO.getCommentId());
        commentSavedVO.setPostId(commentReplyDTO.getPostId());
        commentSavedVO.setCreatedAt(Instant.now());
        return commentSavedVO;
    }

    public List<Map<Short, Object>> GetAllCommentsByPostId(Long postId) {
        logger.info("Getting all comments by post id: {}", postId);
        return commentRepository.findCommentsByPostId(postId);
    }

    public List<List<Object>> GetAllCommentsAndReplies(Long postId) {
        logger.info("Getting all comments and replies");
        //get all comments by post id
        List<Map<Short, Object>> commentsList = GetAllCommentsByPostId(postId);
        List<Long> commentIds = new ArrayList<>();
        //get all comment ids from all comments for getting all replies with same comment ids
        for (Map<Short, Object> comment : commentsList) {
            commentIds.add(((BigInteger) comment.get("comment_id")).longValue());
        }
        //get all replies by comment ids
        List<Map<Short, Object>> repliesList = replyService.GetRepliesByCommentId(commentIds);
        //initialize reply page and size
//        int replyPage = pageDTO.getReplyPage() - 1;//index starts from 0
//        int replySize = 2;
//        System.out.println("replyPage: " + replyPage);
//        if(replyPage < 0){
//            replyPage = 0;
//        } else {
//            //continue;
//        }
        //final result list
        List<List<Object>> res = new ArrayList<>();
        //traverse all comments and pick out replies with same comment id
        for (Map<Short, Object> comment : commentsList) {
            //get comment id
            Long commentId = ((BigInteger) comment.get("comment_id")).longValue();
            List<Map<String, Object>> replies = new ArrayList<>();
            //traverse all replies and pick out replies with same comment id got above
            for (Map<Short, Object> reply : repliesList) {
                Map<String, Object> repliesMap = new HashMap<>();
                //set reply map if the comment id is same as the one got above
                Long commentId2 = ((BigInteger) reply.get("comment_id")).longValue();
                if (commentId.equals(commentId2)) {
                    repliesMap.put("reply_id", reply.get("reply_id"));
                    repliesMap.put("comment_id", reply.get("comment_id"));
                    repliesMap.put("from_uid", reply.get("from_uid"));
                    repliesMap.put("fmui_avatar_url", reply.get("fmui_avatar_url"));
                    repliesMap.put("from_username", reply.get("from_username"));
                    repliesMap.put("to_uid", reply.get("to_uid"));
                    repliesMap.put("to_username", reply.get("to_username"));
                    repliesMap.put("content", reply.get("content"));
                    repliesMap.put("created_at", reply.get("created_at").toString());
                    replies.add(repliesMap);
                } else {
                    //continue;
                }

            }
//            if(replies.isEmpty()){
//                continue;
//            } else {
                //paginate replies
//                Pageable pageable = PageRequest.of(replyPage, replySize);
//                int startIdx = (int) pageable.getOffset();
//                int endIdx = Math.min((startIdx + pageable.getPageSize()), replies.size());
//                List<Map<String, Object>> currentReplyItems = replies.subList(startIdx, endIdx);
//                Page<Map<String, Object>> currentRepliesPage = new PageImpl<>(currentReplyItems, pageable, replies.size());

                List<Object> combinedList = new ArrayList<>();
                //add comment and all replies that under the comment
//                combinedList.add(currentRepliesPage.getContent());
//                combinedList.add(currentRepliesPage.getTotalPages());
                //add combined comment and replies to final result list and paginate the result
                combinedList.add(comment);
                combinedList.add(replies);
                res.add(combinedList);
            }

        return res;
    }

    public List<NewestCommentVO> GetNewestComments() {
        logger.info("Getting newest comments");
        try {
            List<Map<Short, Object>> newestCommentsList = commentRepository.findNewestComments();
            if (!newestCommentsList.isEmpty()) {
                logger.info("Newest comments found");
                return TransferToNewestCommentVO(newestCommentsList);
            } else {
                logger.info("No newest comments found");
            }
        } catch (Exception e) {
            logger.error("Failed to get newest comments", e);
        }
        return Collections.emptyList();
    }

    private static List<NewestCommentVO> TransferToNewestCommentVO(List<Map<Short, Object>> newestCommentsList) {
        logger.info("Transferring to newest comment VO");
        List<NewestCommentVO> newestCommentVOList = new ArrayList<>();
        for (Map<Short, Object> map : newestCommentsList) {
            try {
                NewestCommentVO newestCommentVO = new NewestCommentVO();
                newestCommentVO.setPostId((Long) map.get("post_id"));
                newestCommentVO.setContent((String) map.get("content"));
                newestCommentVO.setGameName((String) map.get("game_name"));
                Timestamp timestamp = (Timestamp) map.get("created_at");
                newestCommentVO.setCreatedAt(timestamp.toInstant());
                newestCommentVOList.add(newestCommentVO);
            } catch (Exception e) {
                logger.error("Failed to transfer to newest comment VO", e);
            }
        }
        return newestCommentVOList;
    }
}


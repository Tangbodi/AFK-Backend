package com.example.demo.Service.Replies;

import com.example.demo.Model.DTO.CommentReplyDTO;
import com.example.demo.Repository.ReplyRepository;
import com.example.demo.Model.Entity.PostReply;
import com.example.demo.Model.VO.ReplySavedVO;
import com.example.demo.Service.IP.IpAddressService;
import com.example.demo.Service.Posts.PostInfoService;
import com.example.demo.Util.Snowflake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Service
public class ReplyService {
    private static final Logger logger = LoggerFactory.getLogger(ReplyService.class);
    @Autowired
    private ReplyRepository replyRepository;
    @Autowired
    private IpAddressService ipAddressService;
    @Autowired
    private PostInfoService postInfoService;
    public ReplySavedVO SetReply(CommentReplyDTO commentReplyDTO) {
        logger.info("Setting reply");
        try {
            long replyId = Snowflake.generateUniqueId();
            commentReplyDTO.setReplyId(replyId);
            commentReplyDTO.setCreatedAt(Instant.now());
            PostReply postReply = new PostReply();
            postReply.setId(replyId);
            postReply.setCommentId(commentReplyDTO.getCommentId());
            postReply.setToReplyId(commentReplyDTO.getToReplyId());
            postReply.setContent(commentReplyDTO.getContent());
            postReply.setFromUid(commentReplyDTO.getFromUid());
            postReply.setToUid(commentReplyDTO.getToUid());
            postReply.setCreatedAt(commentReplyDTO.getCreatedAt());
            postReply.setModifiedAt(commentReplyDTO.getCreatedAt());
            PostReply savedReply = replyRepository.save(postReply);
            if (savedReply != null) {
                logger.info("Reply saved successfully");
                ipAddressService.SetCommentReplyIpAddress(commentReplyDTO);
                //update post comment reply count
                postInfoService.UpdatePostCommentReplyCount(commentReplyDTO.getPostId());
                return TransferToVO(commentReplyDTO);
            } else {
                logger.info("Failed to save reply");
            }
        } catch (Exception e) {
            logger.error("Failed to set reply", e);
        }
        return null;
    }

    public List<Map<String, Object>> GetRepliesByCommentId(List<Long> commentIds, Long userId) {
        logger.info("Getting replies by comment id");
        List<Map<String, Object>> replyList = replyRepository.findByCommentId(commentIds,userId);
        return replyList;
    }

    private static ReplySavedVO TransferToVO(CommentReplyDTO commentReplyDTO) {
        logger.info("Transferring reply to VO");
        ReplySavedVO replySavedVO = new ReplySavedVO();
        replySavedVO.setReplyId(commentReplyDTO.getReplyId().toString());
        replySavedVO.setCommentId(commentReplyDTO.getCommentId().toString());
        replySavedVO.setToReplyId(commentReplyDTO.getToReplyId().toString());
        replySavedVO.setToUid(commentReplyDTO.getToUid().toString());
        replySavedVO.setCreatedAt(commentReplyDTO.getCreatedAt());
        return replySavedVO;
    }
}


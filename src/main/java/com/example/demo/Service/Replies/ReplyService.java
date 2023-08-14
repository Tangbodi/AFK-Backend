package com.example.demo.Service.Replies;

import com.example.demo.Mapper.Repository.ReplyRepository;
import com.example.demo.Model.DTO.ReplyDTO;
import com.example.demo.Model.Entity.PostReply;
import com.example.demo.Model.VO.ReplyVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.UUID;

@Service
public class ReplyService {
    private static final Logger logger = LoggerFactory.getLogger(ReplyService.class);
    @Autowired
    private ReplyRepository replyRepository;


    public ReplyVO SetReply(ReplyDTO replyDTO) {
        logger.info("Setting reply");
        try {
            String uuid = UUID.randomUUID().toString();
            replyDTO.setReplyId(uuid);
            PostReply postReply = new PostReply();
            postReply.setReplyId(uuid);
            postReply.setCommentId(replyDTO.getCommentId());
            postReply.setParentReplyId(replyDTO.getParentReplyId());
            postReply.setReplyType(replyDTO.getReplyType());
            postReply.setContent(replyDTO.getContent());
            postReply.setFromUid(replyDTO.getFromUid());
            postReply.setToUid(replyDTO.getToUid());
            postReply.setIpvFour(replyDTO.getIpvFour());
            postReply.setIpvSix(replyDTO.getIpvSix());
            postReply.setCreatedAt(replyDTO.getCreatedAt());
            postReply.setModifiedAt(replyDTO.getCreatedAt());

            PostReply savedReply = replyRepository.save(postReply);
            if (savedReply != null) {
                logger.info("Reply saved successfully");
                return TransferToVO(replyDTO);
            } else {
                logger.info("Failed to save reply");
            }
        } catch (Exception e) {
            logger.error("Failed to set reply", e);
        }
        return null;
    }

    private static ReplyVO TransferToVO(ReplyDTO replyDTO) {
        ReplyVO replyVO = new ReplyVO();
        replyVO.setReplyId(replyDTO.getReplyId());
        replyVO.setCommentId(replyDTO.getCommentId());
        replyVO.setParentReplyId(replyDTO.getParentReplyId());
        replyVO.setToUid(replyDTO.getToUid());
        replyVO.setCreatedAt(replyDTO.getCreatedAt());
        return replyVO;
    }
}


package com.example.demo.Service.Replies;

import com.example.demo.Mapper.Repository.ReplyRepository;
import com.example.demo.Model.DTO.CommentReplyDTO;
import com.example.demo.Model.DTO.IpAddressDTO;
import com.example.demo.Model.Entity.PostReply;
import com.example.demo.Model.VO.ReplyVO;
import com.example.demo.Service.IP.IpAddressService;
import com.example.demo.Service.IP.IpService;
import com.example.demo.Util.UUIDCreator;
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
    public ReplyVO SetReply(CommentReplyDTO commentReplyDTO) {
        logger.info("Setting reply");
        try {
            String uuid = UUIDCreator.CreateUUID();
            commentReplyDTO.setReplyId(uuid);
            commentReplyDTO.setCreatedAt(Instant.now());
            PostReply postReply = new PostReply();
            postReply.setId(uuid);
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
                IpAddressDTO ipAddressDTO = new IpAddressDTO();
                ipAddressDTO.setId(commentReplyDTO.getReplyId());
                ipAddressDTO.setIpvFour(commentReplyDTO.getIpvFour());
                ipAddressDTO.setIpvSix(commentReplyDTO.getIpvSix());
                ipAddressDTO.setCreatedAt(commentReplyDTO.getCreatedAt());
                ipAddressService.SetIpAddress(ipAddressDTO);
                return TransferToVO(commentReplyDTO);
            } else {
                logger.info("Failed to save reply");
            }
        } catch (Exception e) {
            logger.error("Failed to set reply", e);
        }
        return null;
    }

    public List<Map<Short, Object>> GetRepliesByCommentId(List<String> commentIds) {
        List<Map<Short, Object>> replyList = replyRepository.findByCommentId(commentIds);
        return replyList;
    }

    private static ReplyVO TransferToVO(CommentReplyDTO commentReplyDTO) {
        ReplyVO replyVO = new ReplyVO();
        replyVO.setReplyId(commentReplyDTO.getReplyId());
        replyVO.setCommentId(commentReplyDTO.getCommentId());
        replyVO.setToReplyId(commentReplyDTO.getToReplyId());
        replyVO.setToUid(commentReplyDTO.getToUid());
        replyVO.setCreatedAt(commentReplyDTO.getCreatedAt());
        return replyVO;
    }
}


package com.example.demo.Service.Replies;

import com.example.demo.Model.DTO.ObjectUserDTO;
import com.example.demo.Model.Entity.RepliesInfo;
import com.example.demo.Model.Entity.UsersLikeReply;
import com.example.demo.Mapper.Repository.ReplyInfoRepository;
import com.example.demo.Mapper.Repository.UserLikeReplyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

@Service
public class ReplyInfoService {
    private static final Logger logger = LoggerFactory.getLogger(ReplyInfoService.class);
    @Autowired
    private ReplyInfoRepository replyInfoRepository;
    @Autowired
    private UserLikeReplyRepository userLikeReplyRepository;

    public void CalculateReplyTotalLike(List<ObjectUserDTO> objectUserDTOList){
        logger.info("Finding all users like replies list with like status = 1");
        for(ObjectUserDTO objectUserDTO : objectUserDTOList){
            Long replyId = objectUserDTO.getObjectId();
            Map<String,Object> map = userLikeReplyRepository.findReplyTotalLikeByLikeStatus(replyId);
            Integer totalLike= ((BigInteger) map.get("total_like")).intValue();
            UpdateReplyLikeCount(replyId, totalLike);
        }
    }

    private void UpdateReplyLikeCount(Long replyId, Integer totalLike){
        logger.info("Updating reply like count");
        RepliesInfo repliesInfo = replyInfoRepository.findById(replyId)
                .orElseGet(() -> CreateReplyInfo(replyId));
        repliesInfo.setLike(totalLike);
        replyInfoRepository.save(repliesInfo);
        logger.info("Updated reply like count");
    }
    private static RepliesInfo CreateReplyInfo(Long replyId){
        logger.info("Creating reply info");
        RepliesInfo repliesInfo = new RepliesInfo();
        repliesInfo.setId(replyId);
        repliesInfo.setLike(0);
        logger.info("Created reply info for reply ID: {}", replyId);
        return repliesInfo;
    }

}

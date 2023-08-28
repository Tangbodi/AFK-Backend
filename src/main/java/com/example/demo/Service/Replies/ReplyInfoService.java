package com.example.demo.Service.Replies;

import com.example.demo.Model.Entity.CommentsInfo;
import com.example.demo.Model.Entity.RepliesInfo;
import com.example.demo.Repository.ReplyInfoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class ReplyInfoService {
    private static final Logger logger = LoggerFactory.getLogger(ReplyInfoService.class);
    @Autowired
    private ReplyInfoRepository replyInfoRepository;

    public void UpdateReplyLikeCount(Long replyId){
        logger.info("Updating reply like count");
        RepliesInfo repliesInfo = replyInfoRepository.findById(replyId)
                .orElseGet(() -> CreateReplyInfo(replyId));
        repliesInfo.setLike(repliesInfo.getLike()+1);
        replyInfoRepository.save(repliesInfo);
        logger.info("Updated reply like count");
    }
    public RepliesInfo CreateReplyInfo(Long replyId){
        logger.info("Creating reply info");
        RepliesInfo repliesInfo = new RepliesInfo();
        repliesInfo.setId(replyId);
        repliesInfo.setLike(0);
        logger.info("Created reply info for reply ID: {}", replyId);
        return repliesInfo;
    }
}

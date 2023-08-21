package com.example.demo.Service.IP;

import com.example.demo.Mapper.Repository.IpAddressRepository;
import com.example.demo.Model.DTO.CommentReplyDTO;
import com.example.demo.Model.DTO.IpAddressDTO;
import com.example.demo.Model.DTO.PostDTO;
import com.example.demo.Model.Entity.IpAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class IpAddressService {
    private static final Logger logger = LoggerFactory.getLogger(IpAddressService.class);

    @Autowired
    private IpAddressRepository ipAddressRepository;

    @Async("MultiExecutor")
    @Transactional
    public void SetPostIpAddress(PostDTO postDTO) {
        logger.info("Setting IP Address:::");
        try {
            IpAddress ipAddress = new IpAddress();
            ipAddress.setId(postDTO.getPostId());
            ipAddress.setIpvFour(postDTO.getIpvFour());
            ipAddress.setIpvSix(postDTO.getIpvSix());
            ipAddress.setCreatedAt(postDTO.getCreatedAt());
            ipAddress.setModifiedAt(postDTO.getCreatedAt());
            ipAddressRepository.save(ipAddress);
            logger.info("SetIpAddress:::Success:::");
        } catch (Exception e) {
            logger.error("SetIpAddress:::Exception:::" + e.getMessage(), e);
        }
    }
    @Async("MultiExecutor")
    @Transactional
    public void SetCommentReplyIpAddress(CommentReplyDTO commentReplyDTO){
        logger.info("Setting IP Address:::");
        try{
            IpAddress ipAddress = new IpAddress();
            ipAddress.setId(commentReplyDTO.getCommentId());
            ipAddress.setIpvFour(commentReplyDTO.getIpvFour());
            ipAddress.setIpvSix(commentReplyDTO.getIpvSix());
            ipAddress.setCreatedAt(commentReplyDTO.getCreatedAt());
            ipAddress.setModifiedAt(commentReplyDTO.getCreatedAt());
            ipAddressRepository.save(ipAddress);
            logger.info("SetIpAddress:::Success:::");
        } catch (Exception e) {
            logger.error("SetIpAddress:::Exception:::" + e.getMessage(), e);
        }
    }
}

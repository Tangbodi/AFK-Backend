package com.example.demo.Service.UsersInfo;

import com.example.demo.Mapper.Repository.UsersInfoRepository;
import com.example.demo.Model.DTO.UserRegisterDTO;
import com.example.demo.Model.Entity.UsersInfo;
import com.example.demo.Model.VO.UserInfoVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Instant;

@Service
public class UsersInfoService {
    private static final Logger logger = LoggerFactory.getLogger(UsersInfoService.class);
    @Autowired
    private UsersInfoRepository usersInfoRepository;

    public UsersInfo CheckUsernameExists(String username) {
        logger.info("Checking if username exists: {}", username);
        try {
            UsersInfo usersInfo = usersInfoRepository.findByUsername(username).orElse(null);
            logger.info("UsersInfo: {}" + usersInfo.getUsername());
            return usersInfo;
        } catch (Exception e) {
            logger.error("Failed to check username", e);
        }
        return null;
    }

    public UsersInfo CheckEmailExists(String email) {
        logger.info("Checking if email exists: {}", email);
        try {
            UsersInfo usersInfo = usersInfoRepository.findByEmail(email).orElse(null);
            logger.info("UsersInfo: {}" + usersInfo.getEmail());
            return usersInfo;
        } catch (Exception e) {
            logger.error("Failed to check email", e);
        }
        return null;
    }

    @Transactional(rollbackOn = Exception.class)
    public void SetUserInfo(UserRegisterDTO userRegisterDTO, String uuId, Instant instant) {
        logger.info("Setting up UsersInfo: {}");
        try {
            UsersInfo usersInfo = new UsersInfo();
            usersInfo.setUserId(uuId);
            usersInfo.setUsername(userRegisterDTO.getUsername());
            usersInfo.setEmail(userRegisterDTO.getEmail());
            usersInfo.setCreatedAt(instant);
            usersInfo.setModifiedAt(instant);
            usersInfoRepository.save(usersInfo);
        } catch (Exception e) {
            logger.error("Failed to set UsersInfo", e);
        }
    }

    public UserInfoVO GetUserInfo(String username) {
        logger.info("Getting UsersInfo: {}" + username);
        try {
            UsersInfo usersInfo = usersInfoRepository.findByUsername(username).orElse(null);
            logger.info("UsersInfo: {}" + usersInfo);
            UserInfoVO userInfoVO = new UserInfoVO();
            userInfoVO.setUserId(usersInfo.getUserId());
            userInfoVO.setUsername(usersInfo.getUsername());
            userInfoVO.setEmail(usersInfo.getEmail());
            userInfoVO.setPhone(usersInfo.getPhone());
            userInfoVO.setAvatar_url(usersInfo.getAvatarUrl());
            userInfoVO.setCreatedAt(usersInfo.getCreatedAt());
            userInfoVO.setModifiedAt(usersInfo.getModifiedAt());
            return userInfoVO;
        } catch (Exception e) {
            logger.error("Failed to get UsersInfo", e);
        }
        return null;
    }

    @Transactional
    public boolean UpdateUserEmail(String userId, String email) {
        logger.info("Updating email: {}" + userId + "::::::" + email);
        try {
            UsersInfo usersInfo = usersInfoRepository.findById(userId).orElse(null);
            logger.info("Old email: {}" + usersInfo.getEmail());
            usersInfo.setEmail(email);
            logger.info("New email: {}" + email);
            usersInfo.setModifiedAt(Instant.now());
            usersInfoRepository.save(usersInfo);
            logger.info("Updated email successfully: {}");
            return true;
        } catch (Exception e) {
            logger.error("Failed to update UsersInfo", e);
        }
        return false;
    }

}

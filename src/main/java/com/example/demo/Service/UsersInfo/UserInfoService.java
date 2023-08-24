package com.example.demo.Service.UsersInfo;

import com.example.demo.Mapper.Repository.UserInfoRepository;
import com.example.demo.Model.DTO.UserInfoDTO;
import com.example.demo.Model.DTO.UserRegisterDTO;
import com.example.demo.Model.Entity.UsersInfo;
import com.example.demo.Model.VO.UserInfoVO;
import com.example.demo.Service.Redis.RedisEmailService;
import com.example.demo.Service.UsersVerification.UserVerificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Instant;

@Service
public class UserInfoService {
    private static final Logger logger = LoggerFactory.getLogger(UserInfoService.class);
    @Autowired
    private UserInfoRepository userInfoRepository;
    @Autowired
    private RedisEmailService redisEmailService;
    @Lazy
    @Autowired
    private UserVerificationService userVerificationService;

    public UsersInfo CheckUsernameExists(String username) {
        logger.info("Checking if username exists: {}", username);
        try {
            UsersInfo usersInfo = userInfoRepository.findByUsername(username).orElse(null);
            if (usersInfo != null) {
                logger.info("Username: {}" + usersInfo.getUsername());
                return usersInfo;
            } else {
                logger.info("Username does not exist: {}");
            }
        } catch (Exception e) {
            logger.error("Failed to check username: {}", e.getMessage(),e);
        }
        return null;
    }

    public UsersInfo CheckEmailExists(String email) {
        logger.info("Checking if email exists: {}", email);
        try {
            UsersInfo usersInfo = userInfoRepository.findByEmail(email).orElse(null);
            if (usersInfo != null) {
                logger.info("Email: {}" + usersInfo.getEmail());
                return usersInfo;
            } else {
                logger.info("Email does not exist: {}");
            }
        } catch (Exception e) {
            logger.error("Failed to check email: {}", e.getMessage(),e);
        }
        return null;
    }

    @Transactional
    public void SetUserInfo(UserRegisterDTO userRegisterDTO) {
        logger.info("Setting up UsersInfo: {}");
        try {
            UsersInfo usersInfo = new UsersInfo();
            usersInfo.setId(userRegisterDTO.getUserId());
            usersInfo.setUsername(userRegisterDTO.getUsername());
            usersInfo.setEmail(userRegisterDTO.getEmail());
            usersInfo.setCreatedAt(userRegisterDTO.getCreatedAt());
            usersInfo.setModifiedAt(userRegisterDTO.getCreatedAt());
            userInfoRepository.save(usersInfo);
        } catch (Exception e) {
            logger.error("Failed to set UsersInfo: {}", e.getMessage(),e);
        }
    }

    public UserInfoVO GetUserInfoByUsername(String username) {
        logger.info("Getting UsersInfo: {}" + username);
        try {
            UsersInfo usersInfo = userInfoRepository.findByUsername(username).orElse(null);
            if (usersInfo != null) {
                logger.info("UsersInfo: {}" + usersInfo.getUsername());
                UserInfoDTO userInfoDTO = new UserInfoDTO();
                userInfoDTO.setUserId(usersInfo.getId());
                userInfoDTO.setUsername(usersInfo.getUsername());
                userInfoDTO.setEmail(usersInfo.getEmail());
                userInfoDTO.setAvatarUrl(usersInfo.getAvatarUrl());
                userInfoDTO.setCreatedAt(usersInfo.getCreatedAt());
                userInfoDTO.setModifiedAt(usersInfo.getModifiedAt());
                return TransferToVO(userInfoDTO);
            } else {
                logger.info("UserInfo does not exist: {}" );
            }

        } catch (Exception e) {
            logger.error("Failed to get UsersInfo: {}", e.getMessage(),e);
        }
        return null;
    }
    public UserInfoVO GetUserInfoByUserId(Long userId) {
        logger.info("Getting UsersInfo: {}" + userId);
        try {
            UsersInfo usersInfo = userInfoRepository.findById(userId).orElse(null);
            if (usersInfo != null) {
                logger.info("UsersInfo: {}" + usersInfo.getUsername());
                UserInfoDTO userInfoDTO = new UserInfoDTO();
                userInfoDTO.setUserId(usersInfo.getId());
                userInfoDTO.setUsername(usersInfo.getUsername());
                userInfoDTO.setEmail(usersInfo.getEmail());
                userInfoDTO.setAvatarUrl(usersInfo.getAvatarUrl());
                userInfoDTO.setCreatedAt(usersInfo.getCreatedAt());
                userInfoDTO.setModifiedAt(usersInfo.getModifiedAt());
                return TransferToVO(userInfoDTO);
            } else {
                logger.info("UserInfo does not exist: {}" );
            }

        } catch (Exception e) {
            logger.error("Failed to get UsersInfo: {}", e.getMessage(),e);
        }
        return null;
    }
    public UserInfoVO TransferToVO(UserInfoDTO userInfoDTO){
        UserInfoVO userInfoVO = new UserInfoVO();
        userInfoVO.setUserId(userInfoDTO.getUserId());
        userInfoVO.setUsername(userInfoDTO.getUsername());
        userInfoVO.setEmail(userInfoDTO.getEmail());
        userInfoVO.setAvatarUrl(userInfoDTO.getAvatarUrl());
        userInfoVO.setCreatedAt(userInfoDTO.getCreatedAt());
        userInfoVO.setModifiedAt(userInfoDTO.getModifiedAt());
        return userInfoVO;
    }

//    public void CreateRedisCacheForUpdateEmail(String newEmail, String userId, HttpServletRequest request) {
//        logger.info("Creating redis cache for update email: {}" + newEmail);
//        try {
//            String token = redisEmailService.SetUpdateEmailCache(newEmail);
//            userVerificationService.UpdateTokenForUpdateEmail(token, userId, request);
//        } catch (Exception e) {
//            logger.error("Failed to create redis cache for update email: {}", e.getMessage(),e);
//        }
//    }

    @Transactional
    public boolean UpdateUserEmail(String userId, String newEmail) {
        logger.info("Updating email: {}" + userId + "::::::" + newEmail);
        try {
            Long userIdLong = Long.parseLong(userId);
            UsersInfo usersInfo = userInfoRepository.findById(userIdLong).orElse(null);
            if (usersInfo != null) {
                logger.info("UsersInfo: {}" + usersInfo.getUsername());
                logger.info("Old email: {}" + usersInfo.getEmail());
                usersInfo.setEmail(newEmail);
                logger.info("New email: {}" + newEmail);
                usersInfo.setModifiedAt(Instant.now());
                userInfoRepository.save(usersInfo);
                logger.info("Updated email successfully: {}");
                return true;
            } else {
                logger.info("Failed to update email: {}");
            }
        } catch (Exception e) {
            logger.error("Failed to update UsersInfo: {}", e.getMessage(),e);
        }
        return false;
    }
}

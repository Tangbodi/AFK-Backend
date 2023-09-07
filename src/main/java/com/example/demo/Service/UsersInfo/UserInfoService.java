package com.example.demo.Service.UsersInfo;

import com.example.demo.Mapper.Repository.UserInfoRepository;
import com.example.demo.Mapper.Repository.UsersLoginRepository;
import com.example.demo.Model.DTO.ForgotPasswordDTO;
import com.example.demo.Model.DTO.UserInfoDTO;
import com.example.demo.Model.DTO.UpdatePasswordDTO;
import com.example.demo.Model.DTO.UserRegisterDTO;
import com.example.demo.Model.Entity.UsersInfo;
import com.example.demo.Model.Entity.UsersLogin;
import com.example.demo.Model.VO.UserInfoVO;
import com.example.demo.Service.Redis.RedisEmailService;
import com.example.demo.Service.UsersVerification.UserVerificationService;
import org.mindrot.jbcrypt.BCrypt;
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
    @Autowired
    private UsersLoginRepository usersLoginRepository;

    public UsersInfo CheckUsernameExists(String username) {
        logger.info("Checking if username exists: {}", username);
        try {
            UsersInfo usersInfo = userInfoRepository.findByUsername(username).orElse(null);
            if (usersInfo != null) {
                logger.info("Username: {}" + usersInfo.getUsername());
                return usersInfo;
            } else {
                logger.info("Username does not exist: {}");
                return null;
            }
        } catch (Exception e) {
            logger.error("Failed to check username: {}", e.getMessage(), e);
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
                return null;
            }
        } catch (Exception e) {
            logger.error("Failed to check email: {}", e.getMessage(), e);
        }
        return null;
    }
    public UsersInfo GetUserInfoByEmail(String email) {
        logger.info("Getting UsersInfo: {}" + email);
        try {
            UsersInfo usersInfo = userInfoRepository.findByEmail(email).orElse(null);
            if (usersInfo != null) {
                logger.info("UsersInfo: {}" + usersInfo.getUsername());
                UserInfoDTO userInfoDTO = new UserInfoDTO();
                userInfoDTO.setUserId(usersInfo.getId());
                userInfoDTO.setUsername(usersInfo.getUsername());
                userInfoDTO.setEmail(usersInfo.getEmail());
                userInfoDTO.setAvatarUrl(usersInfo.getAvatarUrl());
                userInfoDTO.setCreatedAt(usersInfo.getCreatedAt());
                userInfoDTO.setModifiedAt(usersInfo.getModifiedAt());
                return usersInfo;
            } else {
                logger.info("UserInfo does not exist: {}");
                return null;
            }

        } catch (Exception e) {
            logger.error("Failed to get UsersInfo: {}", e.getMessage(), e);
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
            logger.error("Failed to set UsersInfo: {}", e.getMessage(), e);
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
                logger.info("UserInfo does not exist: {}");
                return null;
            }

        } catch (Exception e) {
            logger.error("Failed to get UsersInfo: {}", e.getMessage(), e);
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
                logger.info("UserInfo does not exist: {}");
                return null;
            }

        } catch (Exception e) {
            logger.error("Failed to get UsersInfo: {}", e.getMessage(), e);
        }
        return null;
    }

    public UserInfoVO TransferToVO(UserInfoDTO userInfoDTO) {
        UserInfoVO userInfoVO = new UserInfoVO();
        userInfoVO.setLongUid(userInfoDTO.getUserId());
        userInfoVO.setUserId(userInfoDTO.getUserId().toString());
        userInfoVO.setUsername(userInfoDTO.getUsername());
        userInfoVO.setEmail(userInfoDTO.getEmail());
        userInfoVO.setAvatarUrl(userInfoDTO.getAvatarUrl());
        userInfoVO.setCreatedAt(userInfoDTO.getCreatedAt());
        userInfoVO.setModifiedAt(userInfoDTO.getModifiedAt());
        return userInfoVO;
    }

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
                return false;
            }
        } catch (Exception e) {
            logger.error("Failed to update UsersInfo: {}", e.getMessage(), e);
        }
        return false;
    }
    @Transactional
    public boolean UpdateUserPassword(UpdatePasswordDTO updatePasswordDTO) {
        logger.info("Updating password: {}");
        try {
            UsersLogin user = usersLoginRepository.findById(updatePasswordDTO.getUserId()).orElse(null);
            logger.info("Found user: {}" + user.getUsername());
            String oldPassword = user.getPassword();
            if (BCrypt.checkpw(updatePasswordDTO.getOldPassword(), oldPassword)) {
                logger.info("Old password is correct: {}" + updatePasswordDTO.getOldPassword());
                String newPassword = BCrypt.hashpw(updatePasswordDTO.getNewPassword(), BCrypt.gensalt());
                user.setPassword(newPassword);
                user.setModifiedAt(Instant.now());
                usersLoginRepository.save(user);
                logger.info("Updated password successfully: {}");
                return true;
            } else {
                logger.info("Old password is incorrect: {}" + updatePasswordDTO.getOldPassword());
                return false;
            }
        } catch (Exception e) {
            logger.error("Failed to update password: {}", e.getMessage(), e);
        }
        return false;
    }
    @Transactional
    public boolean ResetUserPassword(ForgotPasswordDTO forgotPasswordDTO){
        logger.info("Resetting password: {}");
        try{
            UsersLogin user = usersLoginRepository.findById(forgotPasswordDTO.getUserId()).orElse(null);
            logger.info("Found user: {}" + user.getUsername());
            String newPassword = BCrypt.hashpw(forgotPasswordDTO.getNewPassword(), BCrypt.gensalt());
            user.setPassword(newPassword);
            user.setModifiedAt(Instant.now());
            usersLoginRepository.save(user);
            return true;
        } catch (Exception e) {
            logger.error("Failed to reset password: {}", e.getMessage(), e);
        }
        return false;
    }
}

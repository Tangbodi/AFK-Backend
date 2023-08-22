package com.example.demo.Service.UsersAuth;

import com.example.demo.Mapper.Repository.UserAuthRepository;
import com.example.demo.Model.DTO.UserLoginDTO;
import com.example.demo.Model.DTO.UserRegisterDTO;
import com.example.demo.Model.Entity.UsersAuth;
import com.example.demo.Service.UsersInfo.UserInfoService;
import com.example.demo.Service.UsersVerification.UserVerificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Instant;

@Service
public class UserAuthService {
    private static final Logger logger = LoggerFactory.getLogger(UserAuthService.class);
    @Autowired
    private UserAuthRepository userAuthRepository;
    @Lazy
    @Autowired
    private UserVerificationService userVerificationService;
    @Autowired
    private UserInfoService userInfoService;

    @Transactional
    public void SetUsersAuth(UserRegisterDTO userRegisterDTO) {
        logger.info("Setting up UsersAuth :{}");
        try {
            UsersAuth usersAuth = new UsersAuth();
            usersAuth.setId(userRegisterDTO.getUserId());
            usersAuth.setUsername(userRegisterDTO.getUsername());
            usersAuth.setIsVerified(false);
            usersAuth.setIsBlocked(false);
            usersAuth.setCreatedAt(userRegisterDTO.getCreatedAt());
            usersAuth.setModifiedAt(userRegisterDTO.getCreatedAt());
            userAuthRepository.save(usersAuth);
        } catch (Exception e) {
            logger.error("Failed to set UsersAuth: {}", e.getMessage(), e);
        }
    }

    public int CheckUserExistsAndAuth(UserLoginDTO userLoginDTO) {
        logger.info("Checking if username exists: {}", userLoginDTO.getUsername());
        try {
            UsersAuth usersAuth = userAuthRepository.findByUsername(userLoginDTO.getUsername()).orElse(null);
            if (usersAuth == null) {
                logger.info("Username does not exists: {}", userLoginDTO.getUsername());
                return -1;
            } else {
                logger.info("Checking user's verification status: {}", userLoginDTO.getUsername());
                if (usersAuth.getIsVerified() && !usersAuth.getIsBlocked()) {
                    logger.info("Username exists and verified: {}", userLoginDTO.getUsername());
                    //check password

                    //return UsersInfo
                    return 1;
                } else if (usersAuth.getIsBlocked()) {
                    logger.info("Username exists but blocked: {}", userLoginDTO.getUsername());
                    return 2;
                } else {
                    logger.info("Username exists but not verified: {}", userLoginDTO.getUsername());
//                    userVerificationService.SetUserLoginVerificationToken(usersAuth.getUserId(), request);
                    return 0;
                }
            }
        } catch (Exception e) {
            logger.error("Failed to check if username exists: {}", e.getMessage(), e);
        }
        return -2;
    }

    @Transactional
    public boolean UpdateUserAuth(Long userId) {
        logger.info("Updating user's verification status: {}", userId);
        try {
            UsersAuth usersAuth = userAuthRepository.findById(userId).orElse(null);
            if (usersAuth == null) {
                logger.info("User not found with id: {}", userId);
                return false;
            } else {
                usersAuth.setIsVerified(true);
                usersAuth.setModifiedAt(Instant.now());
                userAuthRepository.save(usersAuth);
                logger.info("Updated user's verification status: {}", userId);
                return true;
            }
        } catch (Exception e) {
            logger.error("Failed to update user's verification status via UserAuth: {}", e.getMessage(), e);
        }
        return false;
    }
}

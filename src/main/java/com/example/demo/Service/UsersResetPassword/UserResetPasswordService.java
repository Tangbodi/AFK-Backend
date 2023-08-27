package com.example.demo.Service.UsersResetPassword;

import com.example.demo.Repository.UserResetPasswordRepository;
import com.example.demo.Model.DTO.UserRegisterDTO;
import com.example.demo.Model.Entity.UsersResetPasswordToken;
import com.example.demo.Service.UsersVerification.UserVerificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Instant;

@Service
public class UserResetPasswordService {
    private static final Logger logger = LoggerFactory.getLogger(UserVerificationService.class);

    @Autowired
    private UserResetPasswordRepository userResetPasswordRepository;
    @Async("MultiExecutor")
    @Transactional
    public boolean SetUserResetPasswordToken(String token, Long userId, UserRegisterDTO userRegisterDTO) {
        logger.info("Setting UserResetPasswordToken: {}");
        try {
            UsersResetPasswordToken usersResetPasswordToken = new UsersResetPasswordToken();
            usersResetPasswordToken.setId(userId);
            usersResetPasswordToken.setUsername(userRegisterDTO.getUsername());
            usersResetPasswordToken.setEmail(userRegisterDTO.getEmail());
            usersResetPasswordToken.setCreatedAt(Instant.now());
            usersResetPasswordToken.setModifiedAt(Instant.now());
            userResetPasswordRepository.save(usersResetPasswordToken);
            logger.info("Saved UserResetPasswordToken successfully: {}");
            return true;
        } catch (Exception e) {
            logger.error("Failed to set token: {}", e.getMessage(),e);
        }
        return false;
    }

    @Transactional
    public boolean UpdateUserEmail(Long userId, String email) {
        logger.info("Updating Email: {}" + userId + "::::::" + email);
        try {
            UsersResetPasswordToken usersResetPasswordToken = userResetPasswordRepository.findById(userId).orElse(null);
            logger.info("Old email: {}" + usersResetPasswordToken.getEmail());
            usersResetPasswordToken.setEmail(email);
            logger.info("New email: {}" + email);
            usersResetPasswordToken.setModifiedAt(Instant.now());
            userResetPasswordRepository.save(usersResetPasswordToken);
            logger.info("Updated email successfully: {}");
            return true;
        } catch (Exception e) {
            logger.error("Failed to update email: {}", e.getMessage(),e);
        }
        return false;
    }
}

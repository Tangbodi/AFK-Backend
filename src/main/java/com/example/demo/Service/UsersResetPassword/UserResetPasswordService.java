package com.example.demo.Service.UsersResetPassword;

import com.example.demo.Mapper.Repository.UsersResetPasswordRepository;
import com.example.demo.Model.DTO.UserRegisterDTO;
import com.example.demo.Model.Entity.UsersResetPasswordToken;
import com.example.demo.Service.UsersVerification.UserVerificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Instant;

@Service
public class UserResetPasswordService {
    private static final Logger logger = LoggerFactory.getLogger(UserVerificationService.class);

    @Autowired
    private UsersResetPasswordRepository usersResetPasswordRepository;

    @Transactional
    public boolean SetUserResetPasswordToken(String token, String userId, UserRegisterDTO userRegisterDTO) {
        logger.info("Setting UserResetPasswordToken: {}");
        try {
            UsersResetPasswordToken usersResetPasswordToken = new UsersResetPasswordToken();
            usersResetPasswordToken.setUserId(userId);
            usersResetPasswordToken.setUsername(userRegisterDTO.getUsername());
            usersResetPasswordToken.setEmail(userRegisterDTO.getEmail());
            usersResetPasswordToken.setCreatedAt(Instant.now());
            usersResetPasswordToken.setModifiedAt(Instant.now());
            usersResetPasswordRepository.save(usersResetPasswordToken);
            logger.info("Saved UserResetPasswordToken successfully: {}");
            return true;
        } catch (Exception e) {
            logger.error("Failed to set token: {}", e.getMessage(),e);
        }
        return false;
    }

    @Transactional
    public boolean UpdateUserEmail(String userId, String email) {
        logger.info("Updating Email: {}" + userId + "::::::" + email);
        try {
            UsersResetPasswordToken usersResetPasswordToken = usersResetPasswordRepository.findById(userId).orElse(null);
            logger.info("Old email: {}" + usersResetPasswordToken.getEmail());
            usersResetPasswordToken.setEmail(email);
            logger.info("New email: {}" + email);
            usersResetPasswordToken.setModifiedAt(Instant.now());
            usersResetPasswordRepository.save(usersResetPasswordToken);
            logger.info("Updated email successfully: {}");
            return true;
        } catch (Exception e) {
            logger.error("Failed to update email: {}", e.getMessage(),e);
        }
        return false;
    }
}

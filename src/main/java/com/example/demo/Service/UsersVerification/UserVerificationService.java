package com.example.demo.Service.UsersVerification;

import com.example.demo.Mapper.Repository.UserVerificationRepository;
import com.example.demo.Model.DTO.UserRegisterDTO;
import com.example.demo.Model.Entity.UsersVerificationToken;
import com.example.demo.Service.EmailValidation.ProcessEmailService;
import com.example.demo.Service.Redis.RedisEmailService;
import com.example.demo.Service.UsersAuth.UserAuthService;
import com.example.demo.Service.UsersInfo.UserInfoService;
import com.example.demo.Util.UUIDCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserVerificationService {
    private static final Logger logger = LoggerFactory.getLogger(UserVerificationService.class);
    @Lazy
    @Autowired
    private UserInfoService userInfoService;
    @Autowired
    private UserVerificationRepository userVerificationRepository;
    @Lazy
    @Autowired
    private ProcessEmailService processEmailService;
    @Lazy
    @Autowired
    private UserAuthService userAuthService;
    @Autowired
    private RedisEmailService redisEmailService;


    @Transactional
    public boolean SetUserRegistrationVerificationToken(String token, UserRegisterDTO userRegisterDTO) {
        logger.info("Setting UserRegistrationVerificationToken");
        try {
            UsersVerificationToken usersVerificationToken = new UsersVerificationToken();
            usersVerificationToken.setUserId(userRegisterDTO.getUserId());
            usersVerificationToken.setToken(token);
            usersVerificationToken.setUsername(userRegisterDTO.getUsername());
            usersVerificationToken.setEmail(userRegisterDTO.getEmail());
            usersVerificationToken.setCreatedAt(userRegisterDTO.getCreatedAt());
            usersVerificationToken.setModifiedAt(userRegisterDTO.getCreatedAt());

            userVerificationRepository.save(usersVerificationToken);

            logger.info("Saved UserRegistrationVerificationToken successfully");
            return true;
        } catch (Exception e) {
            logger.error("Failed to set token: {}", e.getMessage(), e);
            return false;
        }
    }


    @Async("MultiExecutor")
    @Transactional
    public void SetUserLoginVerificationToken(String username, HttpServletRequest request) {
        logger.info("request:::"+request.getRequestURL().toString());
        logger.info("Setting UserVerificationToken");
        String siteURL = request.getRequestURL().toString();
        siteURL.replace(request.getServletPath(), "");
        try {
            logger.info("Finding UsersVerificationToken via username: {}", username);
            UsersVerificationToken usersVerificationToken = userVerificationRepository.findByUsername(username);
            if (usersVerificationToken != null) {
                logger.info("Found user: {}", username);
                String token = UUIDCreator.CreateUUID();
                usersVerificationToken.setToken(token);
                usersVerificationToken.setModifiedAt(Instant.now());
                userVerificationRepository.save(usersVerificationToken);
//                String siteURL = request.getRequestURL().toString();
//                siteURL.replace(request.getServletPath(), "");
                logger.info("Saved UserVerificationToken successfully");
                logger.info( usersVerificationToken.getEmail() + "::::::" + token);
                processEmailService.ProcessLoginEmailValidation(siteURL, usersVerificationToken.getEmail(), token, username);
            } else {
                logger.info("User not found: {}", username);
            }
        } catch (Exception e) {
            logger.error("Failed to set token: {}", e.getMessage(), e);
        }
    }


    @Async("MultiExecutor")
    public boolean GetByToken(String token) {
        logger.info("Getting UsersVerificationToken: {}", token);
        try {
           UsersVerificationToken usersVerificationToken = userVerificationRepository.findByToken(token);
            if (usersVerificationToken != null) {
                logger.info("Found UsersVerificationToken: token={}, userId={}", usersVerificationToken.getToken(), usersVerificationToken.getUserId());
                userAuthService.UpdateUserAuth(usersVerificationToken.getUserId());
                redisEmailService.DeleteEmailByToken(token);
                RemoveToken(usersVerificationToken);
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            logger.error("Failed to get UsersVerificationToken: {}", e.getMessage(), e);
        }
        return false;
    }

    @Transactional
    public boolean RemoveToken(UsersVerificationToken usersVerificationToken) {
        logger.info("Removing token via UsersVerificationToken: {}", usersVerificationToken.getToken());
        try {
            usersVerificationToken.setToken(null);
            usersVerificationToken.setModifiedAt(Instant.now());
            userVerificationRepository.save(usersVerificationToken);
            logger.info("Removed token successfully");
            return true;
        } catch (Exception e) {
            logger.error("Failed to remove token: {}", e.getMessage(), e);
            return false;
        }
    }


    @Transactional
    public void UpdateUserEmail(String userId, String email) {
        logger.info("Updating Email for user: userId={}, email={}", userId, email);
        try {
            UsersVerificationToken usersVerificationToken = userVerificationRepository.findById(userId).orElse(null);
            if (usersVerificationToken != null) {
                logger.info("Old email: {}", usersVerificationToken.getEmail());
                logger.info("Updating email to: {}", email);

                usersVerificationToken.setEmail(email);
                usersVerificationToken.setToken(null);
                usersVerificationToken.setModifiedAt(Instant.now());
                userVerificationRepository.save(usersVerificationToken);

                logger.info("Updated email successfully");
            } else {
                logger.info("User not found: {}", userId);
            }
        } catch (Exception e) {
            logger.error("Failed to update email: {}", e.getMessage(), e);
        }
    }

    @Transactional
    public void UpdateTokenForUpdateEmail(String token, String userId, HttpServletRequest request) {
        logger.info("Updating token for update email: token={}, userId={}", token, userId);
        try {
            UsersVerificationToken usersVerificationToken = userVerificationRepository.findById(userId).orElse(null);
            if (usersVerificationToken != null) {
                usersVerificationToken.setToken(token);
                usersVerificationToken.setModifiedAt(Instant.now());
                userVerificationRepository.save(usersVerificationToken);
                logger.info("Updated token for update email successfully");

                processEmailService.ProcessUpdateEmailValidation(request, token, usersVerificationToken.getEmail());
            } else {
                logger.info("User not found: {}", userId);
            }
        } catch (Exception e) {
            logger.error("Failed to update token for update email: {}", e.getMessage(), e);
        }
    }

}

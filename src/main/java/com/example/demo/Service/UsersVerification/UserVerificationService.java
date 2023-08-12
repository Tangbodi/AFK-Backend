package com.example.demo.Service.UsersVerification;

import com.example.demo.Mapper.Repository.UsersVerificationRepository;
import com.example.demo.Model.DTO.UserRegisterDTO;
import com.example.demo.Model.Entity.UsersVerificationToken;
import com.example.demo.Service.EmailValidation.ProcessEmailService;
import com.example.demo.Service.UsersAuth.UserAuthService;
import com.example.demo.Service.UsersInfo.UserInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
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
    private UsersVerificationRepository usersVerificationRepository;
    @Lazy
    @Autowired
    private ProcessEmailService processEmailService;
    @Lazy
    @Autowired
    private UserAuthService userAuthService;


    @Transactional
    public boolean SetUserRegistrationVerificationToken(String token, String userId, UserRegisterDTO userRegisterDTO) {
        logger.info("Setting UserRegistrationVerificationToken");
        try {
            UsersVerificationToken usersVerificationToken = new UsersVerificationToken();
            usersVerificationToken.setUserId(userId);
            usersVerificationToken.setToken(token);
            usersVerificationToken.setUsername(userRegisterDTO.getUsername());
            usersVerificationToken.setEmail(userRegisterDTO.getEmail());
            usersVerificationToken.setCreatedAt(Instant.now());
            usersVerificationToken.setModifiedAt(Instant.now());

            usersVerificationRepository.save(usersVerificationToken);

            logger.info("Saved UserRegistrationVerificationToken successfully");
            return true;
        } catch (Exception e) {
            logger.error("Failed to set token: {}", e.getMessage(), e);
            return false;
        }
    }


    @Transactional
    public void SetUserLoginVerificationToken(String userId, HttpServletRequest request) {
        try {
            logger.info("Finding user via UsersVerificationToken: {}", userId);
            Optional<UsersVerificationToken> optionalToken = usersVerificationRepository.findById(userId);

            if (optionalToken.isPresent()) {
                UsersVerificationToken usersVerificationToken = optionalToken.get();
                logger.info("Found user: {}", usersVerificationToken.getUserId());
                logger.info("Setting UserVerificationToken");

                String token = UUID.randomUUID().toString();
                usersVerificationToken.setToken(token);
                usersVerificationToken.setModifiedAt(Instant.now());

                processEmailService.ProcessLoginEmailValidation(request, usersVerificationToken.getEmail(), token);
            } else {
                logger.info("User not found: {}", userId);
            }
        } catch (Exception e) {
            logger.error("Failed to set token: {}", e.getMessage(), e);
        }
    }


    public boolean GetByToken(String token) {
        logger.info("Getting UsersVerificationToken: {}", token);
        try {
            Optional<UsersVerificationToken> optionalToken = usersVerificationRepository.findByToken(token);
            if (optionalToken.isPresent()) {
                UsersVerificationToken usersVerificationToken = optionalToken.get();
                logger.info("Found UsersVerificationToken: token={}, userId={}", usersVerificationToken.getToken(), usersVerificationToken.getUserId());

                userAuthService.UpdateUserAuth(usersVerificationToken.getUserId());
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
            usersVerificationRepository.save(usersVerificationToken);
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
            UsersVerificationToken usersVerificationToken = usersVerificationRepository.findById(userId).orElse(null);
            if (usersVerificationToken != null) {
                logger.info("Old email: {}", usersVerificationToken.getEmail());
                logger.info("Updating email to: {}", email);

                usersVerificationToken.setEmail(email);
                usersVerificationToken.setToken(null);
                usersVerificationToken.setModifiedAt(Instant.now());
                usersVerificationRepository.save(usersVerificationToken);

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
            UsersVerificationToken usersVerificationToken = usersVerificationRepository.findById(userId).orElse(null);
            if (usersVerificationToken != null) {
                usersVerificationToken.setToken(token);
                usersVerificationToken.setModifiedAt(Instant.now());
                usersVerificationRepository.save(usersVerificationToken);
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

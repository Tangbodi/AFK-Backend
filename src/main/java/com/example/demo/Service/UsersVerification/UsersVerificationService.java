package com.example.demo.Service.UsersVerification;

import com.example.demo.Mapper.Repository.UsersVerificationRepository;
import com.example.demo.Model.DTO.UserRegisterDTO;
import com.example.demo.Model.Entity.UsersVerificationToken;
import com.example.demo.Service.EmailValidation.ProcessEmailService;
import com.example.demo.Service.UsersAuth.UsersAuthService;
import com.example.demo.Service.UsersInfo.UsersInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.time.Instant;
import java.util.UUID;

@Service
public class UsersVerificationService {
    private static final Logger logger = LoggerFactory.getLogger(UsersVerificationService.class);
    @Autowired
    private UsersInfoService usersInfoService;
    @Autowired
    private UsersVerificationRepository usersVerificationRepository;
    @Lazy
    @Autowired
    private ProcessEmailService processEmailService;
    @Lazy
    @Autowired
    private UsersAuthService usersAuthService;

    @Transactional
    public boolean SetUserRegistrationVerificationToken(String token, String userId, UserRegisterDTO userRegisterDTO) {
        logger.info("Setting UserRegistrationVerificationToken: {}");
        try {
            UsersVerificationToken usersVerificationToken = new UsersVerificationToken();
            usersVerificationToken.setUserId(userId);
            usersVerificationToken.setToken(token);
            usersVerificationToken.setUsername(userRegisterDTO.getUsername());
            usersVerificationToken.setEmail(userRegisterDTO.getEmail());
            usersVerificationToken.setCreatedAt(Instant.now());
            usersVerificationToken.setModifiedAt(Instant.now());
            usersVerificationRepository.save(usersVerificationToken);
            logger.info("Saved UserRegistrationVerificationToken successfully: {}");
            return true;
        } catch (Exception e) {
            logger.error("Failed to set token", e);
        }
        return false;
    }

    @Transactional
    public void SetUserLoginVerificationToken(String userId, HttpServletRequest request) {
        try {
            logger.info("Finding user via UsersVerificationToken: {}" + userId);
            UsersVerificationToken usersVerificationToken = usersVerificationRepository.findById(userId).get();
            logger.info("Found user: {}" + usersVerificationToken.getUserId());
            logger.info("Setting UserVerificationToken: {}");
            UUID uuid = UUID.randomUUID();
            String token = uuid.toString();
            usersVerificationToken.setToken(token);
            usersVerificationToken.setModifiedAt(Instant.now());

            processEmailService.ProcessLoginEmailValidation(request, usersVerificationToken.getEmail(), token);

        } catch (Exception e) {
            logger.error("Failed to set token", e);
        }
    }

    public boolean GetByToken(String token) {
        logger.info("Getting UsersVerificationToken: {}" + token);
        UsersVerificationToken usersVerificationToken = new UsersVerificationToken();
        try {
            usersVerificationToken = usersVerificationRepository.findByToken(token).orElse(null);
            if (usersVerificationToken != null) {
                logger.info("Found UsersVerificationToken: {}" + usersVerificationToken.getToken() + "::::::userId::::::" + usersVerificationToken.getUserId());
                //update token
                usersAuthService.UpdateUserAuth(usersVerificationToken.getUserId());
                RemoveToken(usersVerificationToken);
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            logger.error("Failed to get UsersVerificationToken", e);
        }
        return false;
    }

    @Transactional
    public boolean RemoveToken(UsersVerificationToken usersVerificationToken) {
        logger.info("Removing token via UsersVerificationToken: {}" + usersVerificationToken.getToken());
        try {
            usersVerificationToken.setToken(null);
            usersVerificationToken.setModifiedAt(Instant.now());
            usersVerificationRepository.save(usersVerificationToken);
            logger.info("Removed token successfully: {}");
            return true;
        } catch (Exception e) {
            logger.error("Failed to remove token", e);
        }
        return false;
    }

    @Transactional
    public boolean UpdateUserEmail(String userId, String email) {
        logger.info("Updating Email: {}" + userId + "::::::" + email);
        try {
            UsersVerificationToken usersVerificationToken = usersVerificationRepository.findById(userId).orElse(null);
            logger.info("Old email: {}" + usersVerificationToken.getEmail());
            usersVerificationToken.setEmail(email);
            logger.info("New email: {}" + email);
            usersVerificationToken.setModifiedAt(Instant.now());
            usersVerificationRepository.save(usersVerificationToken);
            logger.info("Updated email successfully: {}");
            return true;
        } catch (Exception e) {
            logger.error("Failed to update email", e);
        }
        return false;
    }
}

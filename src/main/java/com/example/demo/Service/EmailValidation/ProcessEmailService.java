package com.example.demo.Service.EmailValidation;

import com.example.demo.Model.DTO.UserRegisterDTO;
import com.example.demo.Service.Redis.RedisEmailService;
import com.example.demo.Service.Redis.RedisUsernameService;
import com.example.demo.Service.UsersVerification.UserVerificationService;
import com.example.demo.Util.UUIDCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.UUID;

@Service
public class ProcessEmailService {
    private static final Logger logger = LoggerFactory.getLogger(ProcessEmailService.class);
    @Lazy
    @Autowired
    private UserVerificationService userVerificationService;
    @Autowired
    private SendEmailService sendEmailService;
    @Autowired
    private RedisEmailService redisEmailService;
    @Autowired
    private RedisUsernameService redisUsernameService;
    @Async("MultiExecutor")
    public void ProcessRegistrationEmailValidation(HttpServletRequest request, UserRegisterDTO userRegisterDTO) {
        logger.info("Processing registration email validation: {}");
        try {
            String token = UUIDCreator.CreateUUID();
//            siteURL = siteURL.replace("http://", "https://");
            if (userVerificationService.SetUserRegistrationVerificationToken(token, userRegisterDTO)) {
                String recipientEmail = userRegisterDTO.getEmail();
                String siteURL = request.getRequestURL().toString();
                siteURL.replace(request.getServletPath(), "");
                String emailValidationLink = siteURL + "/email-validation?token=" + token;
                redisEmailService.SetEmailValidationCacheByToken(token, recipientEmail);
                sendEmailService.sendEmailValidationLink(recipientEmail, emailValidationLink);
            } else {
                logger.info("Failed to set user registration verification token");
            }
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void ProcessLoginEmailValidation(HttpServletRequest request, String email, String token,String username) {
        logger.info("Processing login email validation: {}");
        try {
            String recipientEmail = email;
            String siteURL = request.getRequestURL().toString();
            siteURL.replace(request.getServletPath(), "");
            String emailValidationLink = siteURL + "/email-validation?token=" + token+"/username="+username;
            logger.info("emailValidationLink:::" + emailValidationLink);
            redisEmailService.SetEmailValidationCacheByToken(token, recipientEmail);
            redisUsernameService.SetUserEmailValidationCache(username);
            sendEmailService.sendEmailValidationLink(recipientEmail, emailValidationLink);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public void ProcessUpdateEmailValidation(HttpServletRequest request, String userId, String newEmail) {
        logger.info("Processing update email validation: {}");
        try {
            String recipientEmail = newEmail;
            String siteURL = request.getRequestURL().toString();
            siteURL.replace(request.getServletPath(), "");
            String emailValidationLink = siteURL + "/email-validation?token=" + userId;
            redisEmailService.SetEmailValidationCacheByToken(userId,newEmail);
            sendEmailService.sendEmailValidationLink(recipientEmail, emailValidationLink);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}

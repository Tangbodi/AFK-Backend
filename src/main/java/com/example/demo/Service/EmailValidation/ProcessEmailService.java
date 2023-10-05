package com.example.demo.Service.EmailValidation;

import com.example.demo.Model.DTO.EmailDTO;
import com.example.demo.Model.DTO.UserRegisterDTO;
import com.example.demo.Service.Redis.RedisEmailService;
import com.example.demo.Service.Redis.RedisUsernameService;
import com.example.demo.Service.UsersVerification.UserVerificationService;
import com.example.demo.Util.UUIDCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import redis.clients.jedis.exceptions.JedisConnectionException;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;

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

    public void ProcessRegistrationEmailValidation(UserRegisterDTO userRegisterDTO) {
        logger.info("Processing registration email validation: {}");
        try {
            String token = UUIDCreator.CreateUUID();
//            siteURL = siteURL.replace("http://", "https://");
            if (userVerificationService.SetUserRegistrationVerificationToken(token, userRegisterDTO)) {
                String recipientEmail = userRegisterDTO.getEmail();
                String emailValidationLink = userRegisterDTO.getSiteURL() + "/email-validation?token=" + token;
                logger.info("emailValidationLink:::" + emailValidationLink);
                redisEmailService.SetEmailValidationCacheByToken(token, recipientEmail);
                sendEmailService.sendEmailValidationLink(recipientEmail, emailValidationLink);
            } else {
                logger.info("Failed to set user registration verification token");
            }
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new RuntimeException("Failed to set user registration verification token " + e);
            // throw new Exception("NullPointerException");
        }
    }

    public void ProcessLoginEmailValidation(String siteURL, String email, String token, String username) {
        logger.info("Processing login email validation: {}");
        try {
            String recipientEmail = email;
            String emailValidationLink = siteURL + "/email-validation?token=" + token + "&username=" + username;
            logger.info("emailValidationLink:::" + emailValidationLink);
            redisEmailService.SetEmailValidationCacheByToken(token, recipientEmail);
            //set email validation cache for duplicate request
            redisEmailService.SetEmailValidationCache(email);
            sendEmailService.sendEmailValidationLink(recipientEmail, emailValidationLink);
        } catch (MessagingException | UnsupportedEncodingException | JedisConnectionException e) {
            logger.error("Failed to process login email validation: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to process login email validation " + e);
        }
    }

    public void ProcessUpdateEmailValidation(EmailDTO emailDTO) {
        logger.info("Processing update email validation: {}");
        try {
            String recipientEmail = emailDTO.getEmail();
            String emailValidationLink = emailDTO.getSiteURL() + "/email-validation?token=" + emailDTO.getUserId();
            redisEmailService.SetEmailValidationCacheByToken(emailDTO.getUserId(), emailDTO.getEmail());
            sendEmailService.sendEmailValidationLink(recipientEmail, emailValidationLink);
        } catch (MessagingException | UnsupportedEncodingException e) {
            logger.error("Failed to process update email validation: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to process update email validation " + e);
        }
    }
    public void ProcessForgotPasswordEmailValidation(EmailDTO emailDTO){
        logger.info("Processing forgot password email validation: {}");
        try{
            //The userId is actually the token just named userId for convenience
            String token = emailDTO.getUserId();
            String emailValidationLink = emailDTO.getSiteURL() + "/reset-password?token=" + token;
            redisEmailService.SetEmailValidationCacheByToken(token, emailDTO.getEmail());
            redisEmailService.SetEmailValidationCache(emailDTO.getEmail());
            sendEmailService.sendEmailValidationLink(emailDTO.getEmail(), emailValidationLink);
        }catch (Exception e){
            logger.error("Failed to process forgot password email validation: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to process forgot password email validation " + e);
        }
    }
}

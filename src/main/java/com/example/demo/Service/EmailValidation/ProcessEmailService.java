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
                sendEmailService.sendEmailValidationLink(recipientEmail, emailValidationLink);
                redisEmailService.SetEmailByToken(token, recipientEmail);
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
    public void ProcessLoginEmailValidation( String siteURL, String email, String token,String username) {
        logger.info("Processing login email validation: {}");
        try {
            String recipientEmail = email;
//            String siteURL = request.getRequestURL().toString();
//            siteURL.replace(request.getServletPath(), "");
            String emailValidationLink = siteURL + "/email-validation?token=" + token;
            logger.info("emailValidationLink:::" + emailValidationLink);
            sendEmailService.sendEmailValidationLink(recipientEmail, emailValidationLink);
            redisEmailService.SetEmailByToken(token, recipientEmail);
            redisUsernameService.SetUsernameExistsCache(username);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public void ProcessUpdateEmailValidation(HttpServletRequest request, String token, String newEmail) {
        logger.info("Processing update email validation: {}");
        try {
            String recipientEmail = newEmail;
            String siteURL = request.getRequestURL().toString();
            siteURL.replace(request.getServletPath(), "");
            String emailValidationLink = siteURL + "/email-validation?token=" + token;
            sendEmailService.sendEmailValidationLink(recipientEmail, emailValidationLink);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}

package com.example.demo.Service.EmailValidation;

import com.example.demo.Model.DTO.UserRegisterDTO;
import com.example.demo.Service.UsersVerification.UsersVerificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.UUID;

@Service
public class ProcessEmailService {
    private static final Logger logger = LoggerFactory.getLogger(ProcessEmailService.class);
    @Lazy
    @Autowired
    private UsersVerificationService usersVerificationService;
    @Autowired
    private SendEmailService sendEmailService;

    public boolean ProcessRegistrationEmailValidation(HttpServletRequest request, String userId, UserRegisterDTO userRegisterDTO) {
        logger.info("Processing registration email validation: {}");
        try {
            UUID uuid = UUID.randomUUID();
            String token = uuid.toString();
//            siteURL = siteURL.replace("http://", "https://");
            if (usersVerificationService.SetUserRegistrationVerificationToken(token, userId, userRegisterDTO)) {
                String recipientEmail = userRegisterDTO.getEmail();
                String siteURL = request.getRequestURL().toString();
                siteURL.replace(request.getServletPath(), "");
                String emailValidationLink = siteURL + "/email-validation?token=" + token;
                sendEmailService.sendEmailValidationLink(recipientEmail, emailValidationLink);
                logger.info("sent emailValidationLink: {}" + emailValidationLink);
                return true;
            }
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    public void ProcessLoginEmailValidation(HttpServletRequest request, String email, String token) {
        logger.info("Processing login email validation: {}");
        try {
            String recipientEmail = email;
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
    public void ProcessUpdateEmailValidation(HttpServletRequest request, String token, String newEmail){
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

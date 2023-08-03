package com.example.demo.Service.EmailValidation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;

@Service
public class SendEmailService {
    private static final Logger logger = LoggerFactory.getLogger(SendEmailService.class);
    @Autowired
    private JavaMailSender javaMailSender;

    public void sendEmailValidationLink(String recipientEmail, String emailValidationLink) throws MessagingException, UnsupportedEncodingException {
        logger.info("Editing email sender, receiver, subject, and content: {}");
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message);
            mimeMessageHelper.setFrom("contactus@nybing.com", "NYBing");
            mimeMessageHelper.setTo(recipientEmail);
            String subject = " Verify your email to start using NYBing";
            String content = "<p>Hello,</p>"
                    + "<p>Verify your email address so we know it’s really you—and so we can send you important information about your NYBing account.</p>"
                    + "<br>"
                    + "<p >"
                    + "<a href=\"" + emailValidationLink + "\" style=\"background-color: #c67c4b;padding: 10px 15px;color: white; border-radius: 0.8rem; display: inline-block;text-decoration: none;\">Verify email address</a></p>"
                    + "<br>"
                    + "<p>NYBing</p>";
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setText(content, true);
            javaMailSender.send(message);
            logger.info("sent emailValidationLink: {}" + emailValidationLink);
        } catch (MessagingException e) {
            logger.error("Failed to send email validation link: {}", e.getMessage(),e);
        }
    }
}

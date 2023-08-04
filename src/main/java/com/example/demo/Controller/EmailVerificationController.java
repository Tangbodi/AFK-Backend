package com.example.demo.Controller;

import com.example.demo.Service.Redis.RedisEmailService;
import com.example.demo.Service.UsersInfo.UserInfoService;
import com.example.demo.Service.UsersVerification.UserVerificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Controller
public class EmailVerificationController {
    private static final Logger logger = LoggerFactory.getLogger(EmailVerificationController.class);
    @Autowired
    private UserVerificationService userVerificationService;
    @Autowired
    private RedisEmailService redisEmailService;
    @Autowired
    private UserInfoService userInfoService;

    @GetMapping("/user/registration/email-validation")
    public void ShowEmailValidationPageViaRegisterLink(HttpServletRequest request, @RequestParam(value = "token") String token, HttpServletResponse response) throws IOException {
        boolean isRedirected = true;
        HttpSession session = request.getSession();
        session.setAttribute("isRedirected", isRedirected);
        String redirectURL;
        if (userVerificationService.GetByToken(token)) {
            redirectURL = "https://www.nybing.com/email-verified";
        } else {
            redirectURL = "https://www.nybing.com/link-expired";
        }
        isRedirected = (boolean) session.getAttribute("isRedirected");//true
        if (isRedirected) {
            isRedirected = false;
            logger.info("isRedirected: {}" + isRedirected);
            session.setAttribute("isRedirected", isRedirected);//false
            response.sendRedirect(redirectURL);
        }
    }

    @GetMapping("/user/login/email-validation")
    public void ShowEmailValidationPageViaLoginLink(HttpServletRequest request, @RequestParam(value = "token") String token, HttpServletResponse response) throws IOException {
        boolean isRedirected = true;
        HttpSession session = request.getSession();
        session.setAttribute("isRedirected", isRedirected);
        String redirectURL;
        if (userVerificationService.GetByToken(token)) {
            redirectURL = "https://www.nybing.com/email-verified";
        } else {
            redirectURL = "https://www.nybing.com/link-expired";
        }
        isRedirected = (boolean) session.getAttribute("isRedirected");//true
        if (isRedirected) {
            isRedirected = false;
            logger.info("isRedirected: {}" + isRedirected);
            session.setAttribute("isRedirected", isRedirected);//false
            response.sendRedirect(redirectURL);
        }
    }
    @GetMapping("/user/{userId}/update-email/email-validation")
    public void ShowEmailValidationPageViaUpdateEmailLink(HttpServletRequest request, @PathVariable String userId, @RequestParam(value = "token") String token, HttpServletResponse response) throws IOException {
        boolean isRedirected = true;
        HttpSession session = request.getSession();
        session.setAttribute("isRedirected",isRedirected);
        String redirectURL;
        if(redisEmailService.CheckUpdateEmailCache(token)) {
            String newEmail = redisEmailService.GetEmailByToken(token);
            userVerificationService.UpdateUserEmail(userId,newEmail);
            userInfoService.UpdateUserEmail(userId,newEmail);
            redisEmailService.DeleteEmailByToken(token);
            redirectURL = "https://www.nybing.com/email-verified";
        }else{
            redirectURL = "https://www.nybing.com/link-expired";
        }
        isRedirected = (boolean) session.getAttribute("isRedirected");//true
        if(isRedirected){
            isRedirected = false;
            logger.info("isRedirected:::"+isRedirected);
            session.setAttribute("isRedirected",isRedirected);//false
            response.sendRedirect(redirectURL);
        }
    }

}

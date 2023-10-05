package com.example.demo.Controller;

import com.example.demo.Model.Entity.UsersInfo;
import com.example.demo.Service.Redis.RedisEmailService;
import com.example.demo.Service.Redis.RedisService;
import com.example.demo.Service.Redis.RedisUsernameService;
import com.example.demo.Service.UsersInfo.UserInfoService;
import com.example.demo.Service.UsersVerification.UserVerificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Controller
public class EmailVerificationController {
    private static final Logger logger = LoggerFactory.getLogger(EmailVerificationController.class);
    private static final String EMAIL_VALIDATION = "EMAIL_VALIDATION:";
    @Autowired
    private UserVerificationService userVerificationService;
    @Autowired
    private RedisEmailService redisEmailService;
    @Autowired
    private UserInfoService userInfoService;
    @Autowired
    private RedisUsernameService redisUsernameService;
    @Autowired
    private RedisService redisService;

    @GetMapping("/user/registration/email-validation")
    public void ShowEmailValidationPageViaRegisterLink(HttpServletRequest request, @RequestParam(value = "token") String token, HttpServletResponse response) throws IOException {
        boolean isRedirected = true;
        HttpSession session = request.getSession();
        session.setAttribute("isRedirected", isRedirected);
        String redirectURL;
        if(redisService.CacheExists(EMAIL_VALIDATION + token)){
            logger.info("EMAIL_VALIDATION cache exists: {}" + token);
            userVerificationService.FindUserVerificationByToken(token);
            redisEmailService.DeleteEmailValidationCacheByToken(token);
            redirectURL = "https://away-from-keyboard.com/verifysuccess";
        } else {
            redirectURL = "https://away-from-keyboard.com/verifyfailed";
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
    public void ShowEmailValidationPageViaLoginLink(HttpServletRequest request, @RequestParam(value = "token") String token,
                                                    @RequestParam(value = "username") String username, HttpServletResponse response) throws IOException {
        boolean isRedirected = true;
        HttpSession session = request.getSession();
        session.setAttribute("isRedirected", isRedirected);
        String redirectURL;
        logger.info("token:::" + token);
        if(redisService.CacheExists(EMAIL_VALIDATION + token)){
            logger.info("EMAIL_VALIDATION cache exists: {}" + token);
            userVerificationService.FindUserVerificationByToken(token);
            redisEmailService.DeleteEmailValidationCacheByToken(token);
            redisUsernameService.DeleteEmailValidationCacheByUsername(username);
            redirectURL = "https://away-from-keyboard.com/verifysuccess";
        } else {
            redirectURL = "https://away-from-keyboard.com/verifyfailed";
        }
        isRedirected = (boolean) session.getAttribute("isRedirected");//true
        if (isRedirected) {
            isRedirected = false;
            logger.info("isRedirected: {}" + isRedirected);
            session.setAttribute("isRedirected", isRedirected);//false
            response.sendRedirect(redirectURL);
        }
    }
    @GetMapping("/user-info/update-email/email-validation")
    public void ShowEmailValidationPageViaUpdateEmailLink(HttpServletRequest request,@RequestParam(value = "token") String token, HttpServletResponse response) throws IOException {
        boolean isRedirected = true;
        HttpSession session = request.getSession();
        session.setAttribute("isRedirected",isRedirected);
        String redirectURL;
        logger.info("token:::" + token);
        if(redisService.CacheExists(EMAIL_VALIDATION + token)) {
            logger.info("EMAIL_VALIDATION cache exists: {}" + token);
            String newEmail = redisEmailService.GetEmailByToken(token);
            //the token is userId
            userVerificationService.UpdateUserEmail(token,newEmail);
            userInfoService.UpdateUserEmail(token,newEmail);
            redisEmailService.DeleteEmailValidationCacheByToken(token);
            redisUsernameService.DeleteEmailValidationCacheByUsername(token);
            redirectURL = "https://away-from-keyboard.com/verifysuccess";
        }else{
            redirectURL = "https://away-from-keyboard.com/verifyfailed";
        }
        isRedirected = (boolean) session.getAttribute("isRedirected");//true
        if(isRedirected){
            isRedirected = false;
            logger.info("isRedirected:::"+isRedirected);
            session.setAttribute("isRedirected",isRedirected);//false
            response.sendRedirect(redirectURL);
        }
    }
    @GetMapping("/user-info/forgot-password/reset-password")
    public void ShowEmailValidationPageViaResetPasswordLink(HttpServletRequest request,@RequestParam(value = "token") String token, HttpServletResponse response) throws IOException {
        boolean isRedirected = true;
        HttpSession session = request.getSession();
        session.setAttribute("isRedirected",isRedirected);
        String redirectURL;
        if(redisService.CacheExists(EMAIL_VALIDATION + token)) {
            logger.info("EMAIL_VALIDATION cache exists: {}" + token);
            String email = redisEmailService.GetEmailByToken(token);
            logger.info("email:::" + email);
            UsersInfo usersInfo = userInfoService.GetUserInfoByEmail(email);
            redisEmailService.DeleteEmailValidationCacheByToken(token);
            redisEmailService.DeleteEmailValidationCacheByEmail(email);
            redirectURL = "https://www.nybing.com/reset-password?token=" + usersInfo.getId();
        } else{
            redirectURL = "https://away-from-keyboard.com/verifyfailed";
        }
        isRedirected = (boolean) session.getAttribute("isRedirected");//true
        if(isRedirected) {
            isRedirected = false;
            logger.info("isRedirected:::" + isRedirected);
            session.setAttribute("isRedirected", isRedirected);//false
            response.sendRedirect(redirectURL);
        }
    }
}

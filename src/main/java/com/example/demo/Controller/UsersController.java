package com.example.demo.Controller;

import com.example.demo.Constant.Enum.ReturnCode;
import com.example.demo.Model.DTO.UserLoginDTO;
import com.example.demo.Model.DTO.UserRegisterDTO;
import com.example.demo.Model.Entity.UsersLogin;
import com.example.demo.Model.VO.UserInfoVO;
import com.example.demo.Service.EmailValidation.ProcessEmailService;
import com.example.demo.Service.MQ.MQSender;
import com.example.demo.Service.Redis.RedisMessageService;
import com.example.demo.Service.Redis.RedisService;
import com.example.demo.Service.Redis.RedisUsernameService;
import com.example.demo.Service.UserLogin.UserLoginService;
import com.example.demo.Service.UserRegister.UserRegistrationService;
import com.example.demo.Service.UsersAuth.UserAuthService;
import com.example.demo.Service.UsersInfo.UserInfoService;
import com.example.demo.Service.UsersVerification.UserVerificationService;
import com.example.demo.Util.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.HtmlUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@RestController
@Validated
@RequestMapping("/user")
public class UsersController {
    private static final Logger logger = LoggerFactory.getLogger(UsersController.class);
    @Autowired
    private UserRegistrationService userRegistrationService;
    @Autowired
    private ProcessEmailService processEmailService;
    @Autowired
    private UserAuthService userAuthService;
    @Autowired
    private UserInfoService userInfoService;
    @Autowired
    private UserVerificationService userVerificationService;
    @Autowired
    private RedisUsernameService redisUsernameService;
    @Autowired
    private UserLoginService userLoginService;
    @Autowired
    private MQSender mqSender;
    @Autowired
    private RedisMessageService redisMessageService;


    @PostMapping("/registration")
    public ResponseEntity UserRegistration(@Validated @RequestBody UserRegisterDTO userRegisterDTO, HttpServletRequest request) {
        // Encode email for avoiding email scraping and spam bots
        ApiResponse apiResponse;
        String encodedEmail = HtmlUtils.htmlEscape(userRegisterDTO.getEmail());
        logger.info("Encoded email: {}", encodedEmail);
        userRegisterDTO.setEmail(encodedEmail);
        if (!userRegisterDTO.getPassword().equals(userRegisterDTO.getConfirmPassword())) {
            apiResponse = ApiResponse.error(ReturnCode.RC200.getCode(), "Password and confirm password must be the same");
        } else if (redisUsernameService.CheckUsernameExistsCache(userRegisterDTO.getUsername()) || userRegistrationService.CheckUsernameExists(userRegisterDTO.getUsername()) != null) {
            apiResponse = ApiResponse.error(ReturnCode.RC200.getCode(), "Username already exists");
            redisUsernameService.SetUsernameExistsCache(userRegisterDTO.getUsername());
        } else if (userRegistrationService.CheckEmailExists(userRegisterDTO.getEmail()) != null) {
            apiResponse = ApiResponse.error(ReturnCode.RC200.getCode(), "Email already exists");
        } else {
            // If all checks are passed, register user
            logger.info("User doesn't exist");
            try {
                UsersLogin user = userRegistrationService.RegisterUser(userRegisterDTO);
                logger.info("User registered successfully");
                //Setup email validation
                userRegisterDTO.setUserId(user.getId());
                String siteURL = request.getRequestURL().toString();
                siteURL.replace(request.getServletPath(), "");
                userRegisterDTO.setSiteURL(siteURL);
                //Send MQ
                mqSender.SendUserRegistrationMessage(userRegisterDTO);
//                processEmailService.ProcessRegistrationEmailValidation(siteURL, userRegisterDTO);
                apiResponse = ApiResponse.success("User registered successfully and verification email has been sent out, please check your email");
            } catch (Exception e) {
                apiResponse = ApiResponse.error(ReturnCode.RC500.getCode(), e.getMessage());
            }
        }

        return ResponseEntity.status(apiResponse.getCode()).body(apiResponse);
    }

    @PostMapping("/login")
    public ResponseEntity UserLogin(@Validated @RequestBody UserLoginDTO userLoginDTO, HttpServletRequest request) {
        ApiResponse apiResponse;
        // If all checks are passed, check if user exists and user's auth via UsersAuth
        //-2 --- Internal Server Error
        //-1 --- User not found
        //0 --- User found but not verified
        //1 --- User found and verified
        //2 --- User found but blocked
        int res = userAuthService.CheckUserExistsAndAuth(userLoginDTO);
        if (res == -2) {
            apiResponse = ApiResponse.error(ReturnCode.RC500.getCode(), "Internal Server Error");
        } else if (res == -1) {
            apiResponse = ApiResponse.error(ReturnCode.RC200.getCode(), "User not found");
        } else if (res == 0) {
            if (!redisUsernameService.CheckEmailValidationCacheByUsername(userLoginDTO.getUsername())) {
                userVerificationService.SetUserLoginVerificationToken(userLoginDTO.getUsername(), request);
            } else {
                //
            }
            apiResponse = ApiResponse.error(ReturnCode.RC200.getCode(), "User found but not verified, verification email has been sent out, please check your email");
        } else if (res == 2) {
            apiResponse = ApiResponse.error(ReturnCode.RC200.getCode(), "User found but blocked");
        } else {
            if (userLoginService.CheckPassword(userLoginDTO)) {
                UserInfoVO userInfoVO = userInfoService.GetUserInfo(userLoginDTO.getUsername());
                logger.info("Set session attribute: {}" + "userId, " + userInfoVO.getLongUid());
                request.getSession().setAttribute("userId", userInfoVO.getLongUid());
                request.getSession().setAttribute("username", userInfoVO.getUsername());
                userInfoVO.setJSESSIONID(request.getSession().getId());
                logger.info("JSESSIONID: {}" + userInfoVO.getJSESSIONID());
                redisMessageService.GetUnreadMessageByUserId(userInfoVO.getLongUid());
                logger.info("User logged in successfully : {}");
                apiResponse = ApiResponse.success(userInfoVO);
            } else {
                apiResponse = ApiResponse.error(ReturnCode.RC200.getCode(), "User found but password is incorrect");
            }
        }
        return ResponseEntity.status(apiResponse.getCode()).body(apiResponse);
    }


    @PostMapping("/logout")
    public ResponseEntity UserLogout(HttpServletRequest request) {
        logger.info("Logging out");
        redisMessageService.DeleteUnreadMessage((Long) request.getSession().getAttribute("userId"));
        ApiResponse apiResponse = ApiResponse.success("Logged out successfully");
        request.getSession().invalidate();
        return ResponseEntity.status(apiResponse.getCode()).body(apiResponse);
    }
}

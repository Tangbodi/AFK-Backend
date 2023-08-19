package com.example.demo.Controller;

import com.example.demo.Enum.ReturnCode;
import com.example.demo.Model.DTO.UserFavoritePostDTO;
import com.example.demo.Model.DTO.UserInfoDTO;
import com.example.demo.Model.DTO.UserLoginDTO;
import com.example.demo.Model.DTO.UserRegisterDTO;
import com.example.demo.Model.Entity.User;
import com.example.demo.Model.VO.PostInfoVO;
import com.example.demo.Model.VO.UserFavoritePostVO;
import com.example.demo.Model.VO.UserInfoVO;
import com.example.demo.Service.EmailValidation.ProcessEmailService;
import com.example.demo.Service.Posts.PostService;
import com.example.demo.Service.Redis.RedisUsernameService;
import com.example.demo.Service.UserFavoritePost.UserFavoritePostService;
import com.example.demo.Service.UserLogin.UserLoginService;
import com.example.demo.Service.UserRegister.UserRegistrationService;
import com.example.demo.Service.UsersAuth.UserAuthService;
import com.example.demo.Service.UsersInfo.UserInfoService;
import com.example.demo.Service.UsersResetPassword.UserResetPasswordService;
import com.example.demo.Service.UsersVerification.UserVerificationService;
import com.example.demo.Util.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.HtmlUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@RestController
@Validated
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
    private UserResetPasswordService userResetPasswordService;
    @Autowired
    private RedisUsernameService redisUsernameService;
    @Autowired
    private UserLoginService userLoginService;

    @PostMapping("/user/registration")
    public ResponseEntity UserRegistration(@Validated @RequestBody UserRegisterDTO userRegisterDTO, HttpServletRequest request) throws IllegalAccessException, IOException {
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
            User user = userRegistrationService.RegisterUser(userRegisterDTO);
            if (user != null) {
                logger.info("User registered successfully");
                //Setup email validation
                if (processEmailService.ProcessRegistrationEmailValidation(request, user.getUserId(), userRegisterDTO)) {
                    apiResponse = ApiResponse.success("User registered successfully and verification email has been sent out, please check your email");
                } else {
                    apiResponse = ApiResponse.error(ReturnCode.RC500.getCode(), "Internal Server Error");
                }
            } else {
                logger.info("Failed to register user : {}");
                apiResponse = ApiResponse.error(ReturnCode.RC500.getCode(), "Internal Server Error");
            }
        }

        return ResponseEntity.status(apiResponse.getCode()).body(apiResponse);
    }

    @PostMapping("/user/login")
    public ResponseEntity UserLogin(@Validated @RequestBody UserLoginDTO userLoginDTO, HttpServletRequest request, HttpSession session, BindingResult bindingResult) {
        ApiResponse apiResponse;
        // If all checks are passed, check if user exists and user's auth via UsersAuth
        //-2 --- Internal Server Error
        //-1 --- User not found
        //0 --- User found but not verified
        //1 --- User found and verified
        //2 --- User found but blocked
        int res = userAuthService.CheckUserExistsAndAuth(userLoginDTO, request);
        if (res == -2) {
            apiResponse = ApiResponse.error(ReturnCode.RC500.getCode(), "Internal Server Error");
        } else if (res == -1) {
            apiResponse = ApiResponse.error(ReturnCode.RC200.getCode(), "User not found");
        } else if (res == 0) {
            apiResponse = ApiResponse.error(ReturnCode.RC200.getCode(), "User found but not verified, verification email has been sent out, please check your email");
        } else if (res == 2) {
            apiResponse = ApiResponse.error(ReturnCode.RC200.getCode(), "User found but blocked");
        } else {
            if (userLoginService.CheckPassword(userLoginDTO)) {
                UserInfoDTO userInfoDTO = userInfoService.GetUserInfoByUsername(userLoginDTO.getUsername());
                UserInfoVO userInfoVO = userInfoService.TransferToVO(userInfoDTO);
                logger.info("Set session attribute: {}" + "userId, " + userInfoDTO.getUserId());
                session.setAttribute("userId", userInfoDTO.getUserId());
                logger.info("User logged in successfully : {}");
                apiResponse = ApiResponse.success(userInfoVO);
            } else {
                apiResponse = ApiResponse.error(ReturnCode.RC200.getCode(), "User found but password is incorrect");
            }
        }
        return ResponseEntity.status(apiResponse.getCode()).body(apiResponse);
    }


    @PostMapping("/user/logout")
    public ResponseEntity UserLogout(HttpServletRequest request) {
        logger.info("Logging out");
        request.getSession().invalidate();
        ApiResponse apiResponse = ApiResponse.success("Logged out successfully");
        return ResponseEntity.status(apiResponse.getCode()).body(apiResponse);
    }
}

package com.example.demo.Controller;

import com.example.demo.Enum.ReturnCode;
import com.example.demo.Model.DTO.UserInfoDTO;
import com.example.demo.Model.DTO.UserLoginDTO;
import com.example.demo.Model.DTO.UserRegisterDTO;
import com.example.demo.Model.Entity.User;
import com.example.demo.Model.VO.UserInfoVO;
import com.example.demo.Service.EmailValidation.ProcessEmailService;
import com.example.demo.Service.Redis.RedisUsernameService;
import com.example.demo.Service.UserRegister.UserRegistrationService;
import com.example.demo.Service.UsersAuth.UserAuthService;
import com.example.demo.Service.UsersInfo.UserInfoService;
import com.example.demo.Service.UsersResetPassword.UserResetPasswordService;
import com.example.demo.Service.UsersVerification.UserVerificationService;
import com.example.demo.Util.ApiResponse;
import com.example.demo.Util.PasswordValidator;
import com.example.demo.Util.UsernameValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.HtmlUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@RestController
@Validated
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

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
    private RedisUsernameService redisService;

    @PostMapping("/user/registration")
    public ResponseEntity<ApiResponse<String>> UserRegistration(@Validated @RequestBody UserRegisterDTO userRegisterDTO, HttpServletRequest request) throws IllegalAccessException, IOException {
        // Encode email for avoiding email scraping and spam bots
        String encodedEmail = HtmlUtils.htmlEscape(userRegisterDTO.getEmail());
        logger.info("Encoded email: {}", encodedEmail);
        userRegisterDTO.setEmail(encodedEmail);
        // Check if username is valid
        if (!UsernameValidator.ValidUsername(userRegisterDTO.getUsername())) {
            ApiResponse errorResponse = ApiResponse.error(ReturnCode.RC406.getCode(), "Username can't contain special characters");
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(errorResponse);
        } else if (!PasswordValidator.isValidPassword(userRegisterDTO.getPassword())) {
            ApiResponse errorResponse = ApiResponse.error(ReturnCode.RC406.getCode(), "Password must be at least 8 characters long and contain at least one uppercase letter, one lowercase letter, one number and one special character");
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(errorResponse);
        } else if (!userRegisterDTO.getPassword().equals(userRegisterDTO.getConfirmPassword())) {
            ApiResponse errorResponse = ApiResponse.error(ReturnCode.RC406.getCode(), "Password and confirm password must be the same");
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(errorResponse);
        } else if (redisService.CheckUsernameExistsCache(userRegisterDTO.getUsername()) || userRegistrationService.CheckUsernameExists(userRegisterDTO.getUsername()) != null) {
            ApiResponse errorResponse = ApiResponse.error(ReturnCode.RC409.getCode(), "Username already exists");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
        } else if (userRegistrationService.CheckEmailExists(userRegisterDTO.getEmail()) != null) {
            ApiResponse errorResponse = ApiResponse.error(ReturnCode.RC409.getCode(), "Email already exists");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
        } else {
            // If all checks are passed, register user
            logger.info("User doesn't exist: {}");
            User user = userRegistrationService.RegisterUser(userRegisterDTO);
            logger.info("User: {}", user);
            if (user != null) {
                logger.info("User registered successfully : {}");
                //Setup email validation
                if (processEmailService.ProcessRegistrationEmailValidation(request, user.getUserId(), userRegisterDTO)) {
                    ApiResponse apiResponse = ApiResponse.success("User registered successfully and verification email has been sent out, please check your email");
                    return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
                }
            } else {
                ApiResponse errorResponse = ApiResponse.error(ReturnCode.RC500.getCode(), "Internal Server Error");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
            }
        }
        ApiResponse errorResponse = ApiResponse.error(ReturnCode.RC500.getCode(), "Internal Server Error");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    @PostMapping("/user/login")
    public ResponseEntity UserLogin(@Validated @RequestBody UserLoginDTO userLoginDTO, HttpServletRequest request, HttpSession session) {
        if (!UsernameValidator.ValidUsername(userLoginDTO.getUsername()) || !UsernameValidator.UsernameLength(userLoginDTO.getUsername())) {
            ApiResponse errorResponse = ApiResponse.error(ReturnCode.RC404.getCode(), "User not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } else if (!PasswordValidator.isValidPassword(userLoginDTO.getPassword()) || !PasswordValidator.PasswordLength(userLoginDTO.getPassword())) {
            ApiResponse errorResponse = ApiResponse.error(ReturnCode.RC400.getCode(), "Username or password is incorrect");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } else {
            // If all checks are passed, check if user exists and user's auth via UsersAuth
            //-2 --- Internal Server Error
            //-1 --- User not found
            //0 --- User found but not verified
            //1 --- User found and verified
            //2 --- User found but blocked
            int res = userAuthService.CheckUserExistsAndAuth(userLoginDTO.getUsername(), request);
            if (res == -2) {
                ApiResponse errorResponse = ApiResponse.error(ReturnCode.RC500.getCode(), "Internal Server Error");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
            } else if (res == -1) {
                ApiResponse errorResponse = ApiResponse.error(ReturnCode.RC404.getCode(), "User not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            } else if (res == 0) {
                ApiResponse errorResponse = ApiResponse.error(ReturnCode.RC401.getCode(), "User found but not verified, verification email has been sent out, please check your email");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
            } else if (res == 2) {
                ApiResponse errorResponse = ApiResponse.error(ReturnCode.RC401.getCode(), "User found but blocked");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
            } else {
                UserInfoDTO userInfoDTO = userInfoService.GetUserInfoByUsername(userLoginDTO.getUsername());
                UserInfoVO userInfoVO = userInfoService.TransferToVO(userInfoDTO);
                logger.info("Set session attribute: {}"+ "userId, "+ userInfoDTO.getUserId());
                session.setAttribute("userId", userInfoDTO.getUserId());
                logger.info("User logged in successfully : {}");
                ApiResponse<UserInfoVO> apiResponse = ApiResponse.success(userInfoVO);
                return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
            }
        }
    }
    @PostMapping("/user/logout")
    public ResponseEntity UserLogout(HttpServletRequest request)
    {
        logger.info("Logging out");
        request.getSession().invalidate();
        return ResponseEntity.status(HttpStatus.OK).body("Logged out successfully");
    }
}

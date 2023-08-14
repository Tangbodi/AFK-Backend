package com.example.demo.Controller;

import com.example.demo.Enum.ReturnCode;
import com.example.demo.Model.DTO.UserEmailDTO;
import com.example.demo.Model.DTO.UserInfoDTO;
import com.example.demo.Model.DTO.UserMailDTO;
import com.example.demo.Model.VO.UserInfoVO;
import com.example.demo.Model.VO.UserMailAddressVO;
import com.example.demo.Service.UserRegister.UserRegistrationService;
import com.example.demo.Service.UsersInfo.UserInfoService;
import com.example.demo.Service.UsersInfo.UserMailAddressService;
import com.example.demo.Service.UsersVerification.UserVerificationService;
import com.example.demo.Util.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.HtmlUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@RestController
@Validated
public class UsersInfoController {
    private static final Logger logger = LoggerFactory.getLogger(UsersInfoController.class);
    @Autowired
    private UserRegistrationService userRegistrationService;
    @Autowired
    private UserInfoService userInfoService;
    @Autowired
    private UserVerificationService userVerificationService;
    @Autowired
    private UserMailAddressService userMailAddressService;

    @GetMapping("/user/login/user-info/username")
    public ResponseEntity GetUserInfo(HttpSession session) throws IOException {
        logger.info("GetUserInfo:::session:::" + session);
        String userId = (String) session.getAttribute("userId");
        ApiResponse apiResponse;
        if (userId == null) {
            apiResponse = ApiResponse.error(ReturnCode.RC401.getCode(), "Please login to access this page");
        } else {
            UserInfoDTO userInfoDTO = userInfoService.GetUserInfoByUserId(userId);
            UserInfoVO userInfoVO = userInfoService.TransferToVO(userInfoDTO);
            apiResponse = ApiResponse.success(userInfoVO);
        }
        return ResponseEntity.status(apiResponse.getCode()).body(apiResponse);

    }

    @PutMapping("/user/login/user-info/username/update-email")
    public ResponseEntity UpdateUserInfo(@Validated @RequestBody UserEmailDTO userEmailDTO, HttpServletRequest request, HttpSession session) {
        ApiResponse apiResponse;
        String userId = (String) session.getAttribute("userId");
        if (userId == null) {
            apiResponse = ApiResponse.error(ReturnCode.RC401.getCode(), "Please login to access this page");
        } else{
            String encodedEmail = HtmlUtils.htmlEscape(userEmailDTO.getEmail());
            logger.info("Encoded email: {}", encodedEmail);
            if (userRegistrationService.CheckEmailExists(encodedEmail) != null) {
                apiResponse = ApiResponse.error(ReturnCode.RC409.getCode(), "Email already exists");
            } else {
                userEmailDTO.setEmail(encodedEmail);
                //if email doesn't exist, create a token store token and email in redis(600s) and store token in mysql database
                //send a verification email to user's new email address
                //find token and email in redis once user click on verification link
                //if token and email match, update user's email in mysql database(users verification table, users_info table)
                userInfoService.CreateRedisCacheForUpdateEmail(userEmailDTO.getEmail(), userId, request);
                apiResponse = ApiResponse.success(null);
            }
        }
        return ResponseEntity.status(apiResponse.getCode()).body(apiResponse);
    }

    @PutMapping("/user/login/user-info/username/update-mail-address")
    public ResponseEntity UpdateUserMailAddress(@RequestBody UserMailDTO userMailDTO, HttpSession session) {
        ApiResponse apiResponse;
        String userId = (String) session.getAttribute("userId");
        if (userId == null) {
            apiResponse = ApiResponse.error(ReturnCode.RC401.getCode(), "Please login to access this page");
        } else {
            userMailDTO.setUserId(userId);
            userMailAddressService.UpdateUserMailAddress(userMailDTO);
            apiResponse = ApiResponse.success(null);
        }
        return ResponseEntity.status(apiResponse.getCode()).body(apiResponse);
    }

    @GetMapping("/user/login/user-info/username/mail-address")
    public ResponseEntity GetMailAddress(HttpSession session) {
        ApiResponse apiResponse;
        String userId = (String) session.getAttribute("userId");
        if (userId == null) {
            apiResponse = ApiResponse.error(ReturnCode.RC401.getCode(), "Please login to access this page");
        } else {
            UserMailAddressVO userMailAddressVO = userMailAddressService.GetUserMailAddress(userId);
            apiResponse = ApiResponse.success(userMailAddressVO);
        }
        return ResponseEntity.status(apiResponse.getCode()).body(apiResponse);
    }
}

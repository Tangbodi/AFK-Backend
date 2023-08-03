package com.example.demo.Controller;

import com.example.demo.Enum.ReturnCode;
import com.example.demo.Model.DTO.UserEmailDTO;
import com.example.demo.Model.DTO.UserMailDTO;
import com.example.demo.Model.VO.UserInfoVO;
import com.example.demo.Model.VO.UserMailAddressVO;
import com.example.demo.Service.UserRegister.UserRegistrationService;
import com.example.demo.Service.UsersInfo.UsersMailAddressService;
import com.example.demo.Service.UsersInfo.UsersInfoService;
import com.example.demo.Service.UsersVerification.UsersVerificationService;
import com.example.demo.Util.ApiResponse;
import com.example.demo.Util.UserIdValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.HtmlUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RestController
@Validated
public class UsersInfoController {
    private static final Logger logger = LoggerFactory.getLogger(UsersInfoController.class);
    @Autowired
    private UserRegistrationService userRegistrationService;
    @Autowired
    private UsersInfoService usersInfoService;
    @Autowired
    private UsersVerificationService usersVerificationService;
    @Autowired
    private UsersMailAddressService usersMailAddressService;

    @GetMapping("/user/{userId}/user-info")
    public ResponseEntity GetUserInfo(@PathVariable String userId) throws IOException {
        if (UserIdValidator.CheckUserId(userId) == false) {
            ApiResponse errorResponse = ApiResponse.error(ReturnCode.RC404.getCode(), "User not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
        UserInfoVO userInfoVO = usersInfoService.GetUserInfo(userId);
        if (userInfoVO == null) {
            ApiResponse errorResponse = ApiResponse.error(ReturnCode.RC404.getCode(), "User not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } else {
            ApiResponse apiResponse = ApiResponse.success(userInfoVO);
            return ResponseEntity.ok(apiResponse);
        }
    }

    @PutMapping("/user/{userId}/update-email")
    public ResponseEntity UpdateUserInfo(@PathVariable String userId, @Validated @RequestBody UserEmailDTO userEmailDTO, HttpServletRequest request) {
        if (UserIdValidator.CheckUserId(userId) == false) {
            ApiResponse errorResponse = ApiResponse.error(ReturnCode.RC404.getCode(), "User not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
        String encodedEmail = HtmlUtils.htmlEscape(userEmailDTO.getEmail());
        logger.info("Encoded email: {}", encodedEmail);
        if (userRegistrationService.CheckEmailExists(encodedEmail) != null) {
            ApiResponse errorResponse = ApiResponse.error(ReturnCode.RC409.getCode(), "Email already exists");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
        } else {
            userEmailDTO.setEmail(encodedEmail);
            //if email doesn't exist, create a token store token and email in redis(600s) and store token in mysql database
            //send a verification email to user's new email address
            //find token and email in redis once user click on verification link
            //if token and email match, update user's email in mysql database(users verification table, users_info table)
            usersInfoService.CreateRedisCacheForUpdateEmail(userEmailDTO.getEmail(), userId, request);
            ApiResponse apiResponse = ApiResponse.success(null);
            return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
        }
    }

    @PutMapping("/user/{userId}/update-mail-address")
    public ResponseEntity UpdateUserMailAddress(@PathVariable String userId, @RequestBody UserMailDTO userMailDTO) throws IOException {
        if (UserIdValidator.CheckUserId(userId) == false || usersInfoService.GetUserInfo(userId) == null) {
            ApiResponse errorResponse = ApiResponse.error(ReturnCode.RC404.getCode(), "User not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
        userMailDTO.setUserId(userId);
        usersMailAddressService.UpdateUserMailAddress(userMailDTO);
        ApiResponse apiResponse = ApiResponse.success(null);
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @GetMapping("/user/{userId}/mail-address")
    public ResponseEntity GetMailAddress(@PathVariable String userId) {
        if (UserIdValidator.CheckUserId(userId) == false || usersInfoService.GetUserInfo(userId) == null) {
            ApiResponse errorResponse = ApiResponse.error(ReturnCode.RC404.getCode(), "User not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
        UserMailAddressVO userMailAddressVO = usersMailAddressService.GetUserMailAddress(userId);
        ApiResponse apiResponse = ApiResponse.success(userMailAddressVO);
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);

    }
}

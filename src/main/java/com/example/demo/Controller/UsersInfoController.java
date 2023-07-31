package com.example.demo.Controller;

import com.example.demo.Enum.ReturnCode;
import com.example.demo.Model.DTO.UserEmailDTO;
import com.example.demo.Service.UserRegister.UserRegistrationService;
import com.example.demo.Service.UsersInfo.UsersInfoService;
import com.example.demo.Service.UsersVerification.UsersVerificationService;
import com.example.demo.Util.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.HtmlUtils;

import javax.servlet.http.HttpServletRequest;

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
    @PutMapping("/user/{userId}/update-email")
    public ResponseEntity UpdateUserInfo(@PathVariable String userId, @Validated @RequestBody UserEmailDTO userEmailDTO, HttpServletRequest request) {
        String encodedEmail = HtmlUtils.htmlEscape(userEmailDTO.getEmail());
        if (userRegistrationService.CheckEmailExists(encodedEmail) != null) {
            ApiResponse errorResponse = ApiResponse.error(ReturnCode.RC409.getCode(), "Email already exists");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
        } else {
            userEmailDTO.setEmail(encodedEmail);
            //if email doesn't exist, create a token store token and email in redis(1800s) and store token in mysql database
            //send a verification email to user's new email address
            //find token and email in redis once user click on verification link
            //if token and email match, update user's email in mysql database(users verification table, users_info table)
            usersInfoService.CreateRedisCacheForUpdateEmail(userEmailDTO.getEmail(), userId, request);
            ApiResponse apiResponse = ApiResponse.success(null);
            return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
        }
    }
}

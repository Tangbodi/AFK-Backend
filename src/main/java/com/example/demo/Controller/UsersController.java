package com.example.demo.Controller;

import com.example.demo.Model.DTO.UserRegisterDTO;
import com.example.demo.Model.Entity.User;
import com.example.demo.Service.UserRegister.UserRegistrationService;
import com.example.demo.Util.ApiResponse;
import com.example.demo.Validator.PasswordValidation;
import com.example.demo.Validator.UsernameValidation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.ValidationException;

@RestController
public class UsersController {
    private static final Logger logger = LoggerFactory.getLogger(UsersController.class);

    @Autowired
    private UserRegistrationService userRegistrationService;
    @PostMapping("/v1/user/registration")
    @ExceptionHandler(value = {BindException.class, ValidationException.class, MethodArgumentNotValidException.class})
    public ResponseEntity UserRegistration(@Validated @RequestBody UserRegisterDTO userRegisterDTO, HttpServletRequest request){
        // Check if username is valid
        if(!UsernameValidation.ValidUsername(userRegisterDTO.getUsername())){
            ApiResponse errorResponse = ApiResponse.error(406,"Username can't contain special characters","Not Acceptable");
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(errorResponse);
        }else {
            //Check if user is already registered
            if (userRegistrationService.CheckUsernameExists(userRegisterDTO.getUsername()) == null) {
                ApiResponse errorResponse = ApiResponse.error(409, "Username already exists", "Conflict");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
            } else if (userRegistrationService.CheckEmailExists(userRegisterDTO.getEmail()) == null) {
                ApiResponse errorResponse = ApiResponse.error(409, "Email already exists", "Conflict");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
            } else {
                if(!PasswordValidation.ValidPassword(userRegisterDTO.getPassword())){
                    ApiResponse errorResponse = ApiResponse.error(406,"Password must be at least 8 characters long and contain at least one uppercase letter, one lowercase letter, one number and one special character","Not Acceptable");
                    return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(errorResponse);
                }

            }
        }
        // If all checks are passed, register user
        User user = userRegistrationService.RegisterUser(userRegisterDTO);
        if(user != null){
            logger.info("User successfully registered:::");
            ApiResponse apiResponse = ApiResponse.success(user);
            return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
        }else{
            ApiResponse errorResponse = ApiResponse.error(500, "Internal Server Error", "Internal Server Error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}

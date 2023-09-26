package com.example.demo.Controller;

import com.example.demo.Constant.Enum.ReturnCode;
import com.example.demo.Model.DTO.*;
import com.example.demo.Model.VO.*;
import com.example.demo.Service.EmailValidation.ProcessEmailService;
import com.example.demo.Service.MQ.MQSender;
import com.example.demo.Service.Message.MessageService;
import com.example.demo.Service.Posts.PostService;
import com.example.demo.Service.Posts.PostUserMapService;
import com.example.demo.Service.Redis.RedisEmailService;
import com.example.demo.Service.Redis.RedisMessageService;
import com.example.demo.Service.Redis.RedisService;
import com.example.demo.Service.Redis.RedisUserSettingService;
import com.example.demo.Service.UserLikeSave.UserLikeSaveService;
import com.example.demo.Service.UserRegister.UserRegistrationService;
import com.example.demo.Service.UsersInfo.UserInfoService;
import com.example.demo.Service.UsersInfo.UserMailAddressService;
import com.example.demo.Service.UsersVerification.UserVerificationService;
import com.example.demo.Util.ApiResponse;
import com.example.demo.Util.UUIDCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.HtmlUtils;

import javax.jms.JMSException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

@RestController
@Validated
@RequestMapping("/user-info")
public class UsersInfoController {
    private static final Logger logger = LoggerFactory.getLogger(UsersInfoController.class);
    private static final String MESSAGE_MENTION_KEY = "UNREAD:";
    private static final String EMAIL_VALIDATION = "EMAIL_VALIDATION:";
    @Autowired
    private UserRegistrationService userRegistrationService;
    @Autowired
    private UserInfoService userInfoService;
    @Autowired
    private UserVerificationService userVerificationService;
    @Autowired
    private UserMailAddressService userMailAddressService;
    @Autowired
    private PostService postService;
    @Autowired
    private UserLikeSaveService userLikeSaveService;
    @Autowired
    private MessageService messageService;
    @Autowired
    private RedisEmailService redisEmailService;
    @Autowired
    private ProcessEmailService processEmailService;
    @Autowired
    private PostUserMapService postUserMapService;
    @Autowired
    private RedisService redisService;
    @Autowired
    private RedisMessageService redisMessageService;
    @Autowired
    private MQSender mqSender;

    @GetMapping("/")
    public ResponseEntity GetUserInfo(HttpServletRequest request) {
        Long userId = (Long) request.getSession().getAttribute("userId");
        ApiResponse apiResponse;
        if (userId == null) {
            apiResponse = ApiResponse.error(ReturnCode.RC401.getCode(), "Please login to access this page");
        } else {
            UserInfoVO userInfoVO = userInfoService.GetUserInfo(userId);
            apiResponse = ApiResponse.success(userInfoVO);
        }
        return ResponseEntity.status(apiResponse.getCode()).body(apiResponse);

    }

    @PutMapping("/update-avatar")
    public ResponseEntity UpdateUserAvatar(@RequestParam("image") MultipartFile[] images, HttpServletRequest request) {
        Long userId = (Long) request.getSession().getAttribute("userId");
        ApiResponse apiResponse;
        if (userId == null) {
            apiResponse = ApiResponse.error(ReturnCode.RC401.getCode(), "Please login to access this page");
        } else if (images[0].isEmpty()) {
            apiResponse = ApiResponse.error(ReturnCode.RC400.getCode(), "Please select an image to upload");
        } else if (!images[0].isEmpty() && images.length > 1) {
            apiResponse = ApiResponse.error(ReturnCode.RC400.getCode(), "Please select only one image to upload");
        } else {
            try {
                if (userInfoService.UpdateUserAvatar(images, userId)) {
                    apiResponse = ApiResponse.success("Avatar has been updated");
                } else {
                    apiResponse = ApiResponse.error(ReturnCode.RC400.getCode(), "Avatar is not an image or size is too large");
                }
            } catch (Exception e) {
                logger.error("Failed to update avatar", e.getMessage(), e);
                apiResponse = ApiResponse.error(ReturnCode.RC500.getCode(), e.getMessage());
            }
        }
        return ResponseEntity.status(apiResponse.getCode()).body(apiResponse);
    }

    @PutMapping("/update-password")
    public ResponseEntity UpdateUserPassword(@Validated @RequestBody UpdatePasswordDTO updatePasswordDTO, HttpServletRequest request) {
        ApiResponse apiResponse;
        Long userId = (Long) request.getSession().getAttribute("userId");
        if (userId == null) {
            apiResponse = ApiResponse.error(ReturnCode.RC401.getCode(), "Please login to access this page");
        } else {
            if (!updatePasswordDTO.getNewPassword().equals(updatePasswordDTO.getConfirmPassword())) {
                apiResponse = ApiResponse.error(ReturnCode.RC400.getCode(), "New password and confirm password are not the same");
                return ResponseEntity.status(apiResponse.getCode()).body(apiResponse);
            } else {
                updatePasswordDTO.setUserId(userId);
                if (userInfoService.UpdateUserPassword(updatePasswordDTO)) {
                    apiResponse = ApiResponse.success("Password has been updated");
                } else {
                    apiResponse = ApiResponse.error(ReturnCode.RC400.getCode(), "Old password is incorrect");
                }
            }
        }
        return ResponseEntity.status(apiResponse.getCode()).body(apiResponse);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity ResetUserPassword(@Validated @RequestBody EmailDTO emailDTO, HttpServletRequest request) throws JMSException {
        ApiResponse apiResponse;
        String encodedEmail = HtmlUtils.htmlEscape(emailDTO.getEmail());
        String token = UUIDCreator.CreateUUID();
        //Check if the email exists if not means the user doesn't exist
        if (userInfoService.CheckEmailExists(encodedEmail) != null) {
            logger.info("Email exists: {}", encodedEmail);
            if (redisService.CacheExists(EMAIL_VALIDATION + encodedEmail)) {
                //set email validation for duplicate request
                apiResponse = ApiResponse.error(ReturnCode.RC200.getCode(), "Reset password link has been sent out, please check your email");
            } else {
                // Get site URL
                String siteURL = request.getRequestURL().toString();
                siteURL.replace(request.getServletPath(), "");
                //Send MQ
                emailDTO.setSiteURL(siteURL);
                emailDTO.setUserId(token);
                mqSender.SendForgotPasswordMessage(emailDTO);
//                processEmailService.ProcessForgotPasswordEmailValidation(token, siteURL, encodedEmail);
//                redisEmailService.SetEmailValidationCache(encodedEmail);
                apiResponse = ApiResponse.success("Reset password link has been sent out, please check your email");
            }
        } else {
            apiResponse = ApiResponse.error(ReturnCode.RC200.getCode(), "Email doesn't exist");
        }
        return ResponseEntity.status(apiResponse.getCode()).body(apiResponse);
    }

    @PutMapping("/forgot-password/enter-password")
    public ResponseEntity EnterNewPassword(@Validated @RequestBody ForgotPasswordDTO forgotPasswordDTO, @RequestParam(value = "token") Long token) {
        ApiResponse apiResponse;
        //Here token is userId
        forgotPasswordDTO.setUserId(token);
        if (userInfoService.ResetUserPassword(forgotPasswordDTO)) {
            apiResponse = ApiResponse.success("Password has been updated");
        } else {
            apiResponse = ApiResponse.error(ReturnCode.RC500.getCode(), "Failed to update password");
        }
        return ResponseEntity.status(apiResponse.getCode()).body(apiResponse);
    }

    @PutMapping("/update-email")
    public ResponseEntity UpdateUserInfo(@Validated @RequestBody EmailDTO emailDTO, HttpServletRequest request) throws JMSException {
        ApiResponse apiResponse;
        Long userId = (Long) request.getSession().getAttribute("userId");
        if (userId == null) {
            apiResponse = ApiResponse.error(ReturnCode.RC401.getCode(), "Please login to access this page");
        } else {
            String encodedEmail = HtmlUtils.htmlEscape(emailDTO.getEmail());
            logger.info("Encoded email: {}", encodedEmail);
            if (userRegistrationService.CheckEmailExists(encodedEmail) != null) {
                apiResponse = ApiResponse.error(ReturnCode.RC200.getCode(), "Email already exists");
            } else {
                if (!redisService.CacheExists(EMAIL_VALIDATION + userId.toString())) {
                    // Get site URL
                    String siteURL = request.getRequestURL().toString();
                    siteURL.replace(request.getServletPath(), "");
                    //Send MQ
                    emailDTO.setUserId(userId.toString());
                    emailDTO.setSiteURL(siteURL);
                    mqSender.SendUserUpdateEmailMessage(emailDTO);
//                    processEmailService.ProcessUpdateEmailValidation(siteURL, userId.toString(), encodedEmail);
//                    redisEmailService.SetEmailValidationCacheByToken(userId.toString(), encodedEmail);
                } else {
                    //
                }
                apiResponse = ApiResponse.error(ReturnCode.RC200.getCode(), "Email validation has been sent out, please check your email");
            }
        }
        return ResponseEntity.status(apiResponse.getCode()).body(apiResponse);
    }

    @PutMapping("/update-mail-address")
    public ResponseEntity UpdateUserMailAddress(@Validated @RequestBody UserMailDTO userMailDTO, HttpServletRequest request) {
        ApiResponse apiResponse;
        Long userId = (Long) request.getSession().getAttribute("userId");
        if (userId == null) {
            apiResponse = ApiResponse.error(ReturnCode.RC401.getCode(), "Please login to access this page");
        } else {
            userMailDTO.setUserId(userId);
            userMailAddressService.UpdateUserMailAddress(userMailDTO);
            apiResponse = ApiResponse.success(null);
        }
        return ResponseEntity.status(apiResponse.getCode()).body(apiResponse);
    }

    @GetMapping("/mail-address")
    public ResponseEntity GetMailAddress(HttpServletRequest request) {
        ApiResponse apiResponse;
        Long userId = (Long) request.getSession().getAttribute("userId");
        if (userId == null) {
            apiResponse = ApiResponse.error(ReturnCode.RC401.getCode(), "Please login to access this page");
        } else {
            UserMailAddressVO userMailAddressVO = userMailAddressService.GetUserMailAddress(userId);
            apiResponse = ApiResponse.success(userMailAddressVO);
        }
        return ResponseEntity.status(apiResponse.getCode()).body(apiResponse);
    }

    @GetMapping("/post-history")
    public ResponseEntity GetUserPostHistory(HttpServletRequest request,
                                             @RequestParam(value = "page") int page,
                                             @RequestParam(value = "size") int size) {
        ApiResponse apiResponse;
        Long userId = (Long) request.getSession().getAttribute("userId");
        page = page - 1;
        if (userId == null) {
            apiResponse = ApiResponse.error(ReturnCode.RC401.getCode(), "Post history isn't viewable when signed out");
        } else if (page < 0 || size <= 0) {
            apiResponse = ApiResponse.success(null);
        } else {
            List<PostInfoVO> postHistoryVOList = postUserMapService.FindUserPostHistory(userId);
            if (!postHistoryVOList.isEmpty()) {
                Pageable pageable = PageRequest.of(page, size);
                int startIdx = (int) pageable.getOffset();
                int endIdx = Math.min((startIdx + pageable.getPageSize()), postHistoryVOList.size());
                List<PostInfoVO> currentPageItems = postHistoryVOList.subList(startIdx, endIdx);
                Page<PostInfoVO> currentPage = new PageImpl<>(currentPageItems, pageable, postHistoryVOList.size());
                apiResponse = ApiResponse.success(currentPage);
            } else {
                apiResponse = ApiResponse.success(postHistoryVOList);
            }
        }
        return ResponseEntity.status(apiResponse.getCode()).body(apiResponse);
    }


    @GetMapping("/saved-post")
    public ResponseEntity GetSavedPostByUser(@Validated @RequestBody ObjectUserDTO objectUserDTO, HttpServletRequest request) {
        ApiResponse apiResponse;
        Long userId = (Long) request.getSession().getAttribute("userId");
        if (userId == null) {
            apiResponse = ApiResponse.error(ReturnCode.RC401.getCode(), "Sign in to access posts that you’ve liked or saved");
            return ResponseEntity.status(apiResponse.getCode()).body(apiResponse);
        } else {
            objectUserDTO.setUserId(userId);
            List<ShowSavedPostVO> showSavedPostVOList = userLikeSaveService.GetSavedPostByUserId(objectUserDTO);
            apiResponse = ApiResponse.success(showSavedPostVOList);
        }
        return ResponseEntity.status(apiResponse.getCode()).body(apiResponse);
    }

    @GetMapping("/unread-message")
    public ResponseEntity GetUnreadMessages(HttpServletRequest request) {
        ApiResponse apiResponse;
        Long userId = (Long) request.getSession().getAttribute("userId");
        if (userId == null) {
            apiResponse = ApiResponse.success(Collections.emptyList());
        } else {
            if (redisService.CacheExists(MESSAGE_MENTION_KEY + userId)) {
                List<MessageVO> messageVOList = redisMessageService.GetUnreadMessageFromRedis(userId);
                apiResponse = ApiResponse.success(messageVOList);
            } else {
                messageService.GetUnreadMessageByUserId(userId);
                List<MessageVO> messageVOList = redisMessageService.GetUnreadMessageFromRedis(userId);
                apiResponse = ApiResponse.success(messageVOList);
            }
        }
        return ResponseEntity.status(apiResponse.getCode()).body(apiResponse);
    }

    @PutMapping("/mark-all-as-read")
    public ResponseEntity ReadMessages(HttpServletRequest request) {
        ApiResponse apiResponse;
        Long userId = (Long) request.getSession().getAttribute("userId");
        if (userId == null) {
            apiResponse = ApiResponse.error(ReturnCode.RC401.getCode(), "Sign in to see unread messages");
        } else {
            messageService.UpdateMessageUserMap(userId);
            apiResponse = ApiResponse.success("All messages have been marked as read");
        }
        return ResponseEntity.status(apiResponse.getCode()).body(apiResponse);
    }

    @GetMapping("/notification")
    public ResponseEntity GetNotification(HttpServletRequest request) {
        Long userId = (Long) request.getSession().getAttribute("userId");
        ApiResponse apiResponse;
        if (userId == null) {
            apiResponse = ApiResponse.error(ReturnCode.RC401.getCode(), "Please login to access this page");
        } else {
            List<MessageVO> messageVOList = messageService.GetMessageHistoryByUserId(userId);
            apiResponse = ApiResponse.success(messageVOList);
        }
        return ResponseEntity.status(apiResponse.getCode()).body(apiResponse);

    }
}

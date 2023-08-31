package com.example.demo.Controller;

import com.example.demo.Constant.Enum.ReturnCode;
import com.example.demo.Model.DTO.ObjectUserDTO;
import com.example.demo.Model.DTO.UserEmailDTO;
import com.example.demo.Model.DTO.UserMailDTO;
import com.example.demo.Model.VO.*;
import com.example.demo.Service.EmailValidation.ProcessEmailService;
import com.example.demo.Service.Message.MessageService;
import com.example.demo.Service.Posts.PostService;
import com.example.demo.Service.Posts.PostUserMapService;
import com.example.demo.Service.Redis.RedisEmailService;
import com.example.demo.Service.UserLikeSave.UserLikeSaveService;
import com.example.demo.Service.UserRegister.UserRegistrationService;
import com.example.demo.Service.UsersInfo.UserInfoService;
import com.example.demo.Service.UsersInfo.UserMailAddressService;
import com.example.demo.Service.UsersVerification.UserVerificationService;
import com.example.demo.Util.ApiResponse;
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
import org.springframework.web.util.HtmlUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Collections;
import java.util.List;

@RestController
@Validated
@RequestMapping("/user-info")
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

    @GetMapping("/")
    public ResponseEntity GetUserInfo(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        ApiResponse apiResponse;
        if (userId == null) {
            apiResponse = ApiResponse.error(ReturnCode.RC401.getCode(), "Please login to access this page");
        } else {
            UserInfoVO userInfoVO = userInfoService.GetUserInfoByUserId(userId);
            apiResponse = ApiResponse.success(userInfoVO);
        }
        return ResponseEntity.status(apiResponse.getCode()).body(apiResponse);

    }

    @PutMapping("/update-email")
    public ResponseEntity UpdateUserInfo(@Validated @RequestBody UserEmailDTO userEmailDTO, HttpServletRequest request, HttpSession session) {
        ApiResponse apiResponse;
        String userId = (String) session.getAttribute("userId");
        if (userId == null) {
            apiResponse = ApiResponse.error(ReturnCode.RC401.getCode(), "Please login to access this page");
        } else {
            String encodedEmail = HtmlUtils.htmlEscape(userEmailDTO.getEmail());
            logger.info("Encoded email: {}", encodedEmail);
            if (userRegistrationService.CheckEmailExists(encodedEmail) != null) {
                apiResponse = ApiResponse.error(ReturnCode.RC200.getCode(), "Email already exists");
            } else {
                if (!redisEmailService.CheckEmailValidationCacheByToken(userId)) {
                    processEmailService.ProcessUpdateEmailValidation(request, userId, encodedEmail);
                    redisEmailService.SetEmailValidationCacheByToken(userId, encodedEmail);
                } else {
                    //
                }
                apiResponse = ApiResponse.error(ReturnCode.RC200.getCode(), "Email validation has been sent out, please check your email");
            }
        }
        return ResponseEntity.status(apiResponse.getCode()).body(apiResponse);
    }

    @PutMapping("/update-mail-address")
    public ResponseEntity UpdateUserMailAddress(@Validated @RequestBody UserMailDTO userMailDTO, HttpSession session) {
        ApiResponse apiResponse;
        Long userId = (Long) session.getAttribute("userId");
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
    public ResponseEntity GetMailAddress(HttpSession session) {
        ApiResponse apiResponse;
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            apiResponse = ApiResponse.error(ReturnCode.RC401.getCode(), "Please login to access this page");
        } else {
            UserMailAddressVO userMailAddressVO = userMailAddressService.GetUserMailAddress(userId);
            apiResponse = ApiResponse.success(userMailAddressVO);
        }
        return ResponseEntity.status(apiResponse.getCode()).body(apiResponse);
    }

    @GetMapping("/post-history")
    public ResponseEntity GetUserPostHistory(HttpSession session,
                                             @RequestParam(value = "page") int page,
                                             @RequestParam(value = "size") int size) {
        ApiResponse apiResponse;
        Long userId = (Long) session.getAttribute("userId");
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
    public ResponseEntity GetSavedPostByUser(@Validated @RequestBody ObjectUserDTO objectUserDTO, HttpSession session) {
        ApiResponse apiResponse;
        Long userId = (Long) session.getAttribute("userId");
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
    public ResponseEntity GetUnreadMessages(HttpSession session) {
        ApiResponse apiResponse;
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            apiResponse = ApiResponse.success(Collections.emptyList());
        } else {
            //redisService.CacheExists(MESSAGE_MENTION_KEY+userId)
            List<MessageVO> messageVOList = messageService.GetUnreadMessageViaMessageUserMap(userId);
            apiResponse = ApiResponse.success(messageVOList);

        }
        return ResponseEntity.status(apiResponse.getCode()).body(apiResponse);
    }

    @PutMapping("/mark-all-as-read")
    public ResponseEntity ReadMessages(HttpSession session) {
        ApiResponse apiResponse;
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            apiResponse = ApiResponse.error(ReturnCode.RC401.getCode(), "Sign in to see unread messages");
        } else {
            messageService.UpdateMessageUserMap(userId);
            apiResponse = ApiResponse.success(null);
        }
        return ResponseEntity.status(apiResponse.getCode()).body(apiResponse);
    }
}

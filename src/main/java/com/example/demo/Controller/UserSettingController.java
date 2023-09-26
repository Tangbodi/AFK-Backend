package com.example.demo.Controller;

import com.example.demo.Constant.Enum.ReturnCode;
import com.example.demo.Model.DTO.UserSettingDTO;
import com.example.demo.Model.VO.UserSettingVO;
import com.example.demo.Service.MQ.MQSender;
import com.example.demo.Service.Redis.RedisUserSettingService;
import com.example.demo.Util.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.jms.JMSException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserSettingController {
    private static final Logger logger = LoggerFactory.getLogger(UserSettingController.class);
    private static final String USER_SETTING = "USER_SETTING";
    @Autowired
    private MQSender mqSender;
    @Autowired
    private RedisUserSettingService redisUserSettingService;
    @GetMapping("/setting")
    public ResponseEntity GetUserSetting(HttpServletRequest request) throws IOException {
        Long userId = (Long) request.getSession().getAttribute("userId");
        ApiResponse apiResponse;
        if (userId == null) {
            apiResponse = ApiResponse.error(ReturnCode.RC401.getCode(), "Please login to access this page");
        } else {

            List<UserSettingVO> userSettingVOList = redisUserSettingService.GetUserSettingCache(USER_SETTING + ":::" + userId, userId);
            apiResponse = ApiResponse.success(userSettingVOList);
        }
        return ResponseEntity.status(apiResponse.getCode()).body(apiResponse);

    }
}

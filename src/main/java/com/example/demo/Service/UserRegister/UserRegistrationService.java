package com.example.demo.Service.UserRegister;

import com.example.demo.Mapper.Repository.UsersInfoRepository;
import com.example.demo.Mapper.Repository.UserRepository;
import com.example.demo.Model.DTO.UserRegisterDTO;
import com.example.demo.Model.Entity.User;
import com.example.demo.Model.Entity.UsersInfo;
import com.example.demo.Service.UserFavoriteGame.UserFavoriteGameService;
import com.example.demo.Service.UsersAuth.UserAuthService;
import com.example.demo.Service.UsersInfo.UserMailAddressService;
import com.example.demo.Service.UsersInfo.UserInfoService;
import com.example.demo.Service.UsersPostsSetting.UserPostSettingService;
import com.example.demo.Util.UUIDCreator;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Instant;
import java.util.UUID;

@Service
public class UserRegistrationService {
    private static final Logger logger = LoggerFactory.getLogger(UserRegistrationService.class);
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UsersInfoRepository usersInfoRepository;
    @Autowired
    private UserInfoService userInfoService;
    @Autowired
    private UserAuthService userAuthService;
    @Autowired
    private UserPostSettingService userPostSettingService;
    @Autowired
    private UserMailAddressService userMailAddressService;

    public UsersInfo CheckUsernameExists(String username) {
        UsersInfo usersInfo = userInfoService.CheckUsernameExists(username);
        return usersInfo;
    }

    public UsersInfo CheckEmailExists(String email) {
        UsersInfo usersInfo = userInfoService.CheckEmailExists(email);
        return usersInfo;
    }

    @Transactional(rollbackOn = Exception.class)
    public User RegisterUser(UserRegisterDTO userRegisterDTO) {
        logger.info("Registering user: {}", userRegisterDTO.getUsername());
        try {
            logger.info("Creating UUID for user: {}", userRegisterDTO.getUsername());
            String uuid = UUIDCreator.CreateUUID();
            Instant createdAt = Instant.now();
            userRegisterDTO.setUserId(uuid);
            userRegisterDTO.setCreatedAt(createdAt);
            logger.info("Setting up User :{}");
            User user = new User();
            user.setUserId(uuid);
            user.setUsername(userRegisterDTO.getUsername());
            String encodedPassword = BCrypt.hashpw(userRegisterDTO.getPassword(), BCrypt.gensalt());
            user.setPassword(encodedPassword);
            user.setCreatedAt(createdAt);
            user.setModifiedAt(createdAt);
            userAuthService.SetUsersAuth(userRegisterDTO);
            userInfoService.SetUserInfo(userRegisterDTO);
            userMailAddressService.SetUserMailAddress(userRegisterDTO);
            userPostSettingService.SaveSetting(userRegisterDTO);
            return userRepository.save(user);
        } catch (Exception e) {
            logger.error("Failed to register user: {}", e.getMessage(),e);
        }
        return null;
    }
}

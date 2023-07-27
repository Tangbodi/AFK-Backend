package com.example.demo.Service.UserRegister;

import com.example.demo.Mapper.Repository.UsersInfoRepository;
import com.example.demo.Mapper.Repository.UsersRepository;
import com.example.demo.Model.DTO.UserRegisterDTO;
import com.example.demo.Model.Entity.User;
import com.example.demo.Model.Entity.UsersInfo;
import com.example.demo.Service.UsersAuth.UsersAuthService;
import com.example.demo.Service.UsersInfo.UsersInfoService;
import com.example.demo.Service.UsersPostsSetting.UsersPostsSettingService;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class UserRegistrationService {
    private static final Logger logger = LoggerFactory.getLogger(UserRegistrationService.class);
    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private UsersInfoRepository usersInfoRepository;
    @Autowired
    private UsersInfoService usersInfoService;
    @Autowired
    private UsersAuthService usersAuthService;
    @Autowired
    private UsersPostsSettingService usersPostsSettingService;

    public UsersInfo CheckUsernameExists(String username) {
        UsersInfo usersInfo = usersInfoRepository.findByUsername(username);
        return usersInfo;
    }
    public UsersInfo CheckEmailExists(String email) {
        UsersInfo usersInfo = usersInfoRepository.findByEmail(email);
        return usersInfo;
    }
    public User RegisterUser(UserRegisterDTO userRegisterDTO){
        try {
            logger.info("Registering user: {}", userRegisterDTO.getUsername());
            logger.info("Creating UUID for user: {}", userRegisterDTO.getUsername());
            UUID uuid = UUID.randomUUID();
            String uuId = uuid.toString();
            Instant instant = Instant.now();
            logger.info("Setting up users :{}");
            User user = new User();
            user.setUserId(uuId);
            user.setUsername(userRegisterDTO.getUsername());
            String encodedPassword = BCrypt.hashpw(userRegisterDTO.getPassword(), BCrypt.gensalt());
            user.setPassword(encodedPassword);
            user.setCreatedAt(instant);
            user.setModifiedAt(instant);
            usersAuthService.SetUsersAuth(userRegisterDTO, uuId, instant);
            usersInfoService.SetUsersInfo(userRegisterDTO, uuId, instant);
            usersPostsSettingService.SaveSetting(userRegisterDTO, uuId, instant);
            return usersRepository.save(user);
        } catch (Exception e) {
            logger.error("Failed to register user", e);
            throw new RuntimeException("Failed to register user", e);
        }
    }
}

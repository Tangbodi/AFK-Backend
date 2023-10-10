package com.example.demo.Service.UsersInfo;

import com.example.demo.Mapper.Repository.UserAvatarRepository;
import com.example.demo.Mapper.Repository.UserInfoRepository;
import com.example.demo.Mapper.Repository.UsersLoginRepository;
import com.example.demo.Model.DTO.ForgotPasswordDTO;
import com.example.demo.Model.DTO.UpdatePasswordDTO;
import com.example.demo.Model.DTO.UserInfoDTO;
import com.example.demo.Model.DTO.UserRegisterDTO;
import com.example.demo.Model.Entity.UsersInfo;
import com.example.demo.Model.Entity.UsersLogin;
import com.example.demo.Model.VO.UserInfoVO;
import com.example.demo.Service.Redis.RedisEmailService;
import com.example.demo.Service.UsersVerification.UserVerificationService;
import com.example.demo.Util.Snowflake;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;

@Service
public class UserInfoService {
    private static final Logger logger = LoggerFactory.getLogger(UserInfoService.class);
    //    private static final String AVATAR_URL = "http://31.220.21.110:8180/IMAGE/AVATAR/";
    private static final String AVATAR_URL = "https://www.away-from-keyboard.com/IMAGE/AVATAR/";
    private static final String TOMCAT_AVATAR_PATH = "/opt/tomcat2/webapps/IMAGE/AVATAR/";
    private static final String USER_SETTING = "USER_SETTING";

    //    private static final String NGINX_AVATAR_PATH = "/usr/local/nginx2/html/IMAGE/AVATAR/";
    @Autowired
    private UserInfoRepository userInfoRepository;
    @Autowired
    private RedisEmailService redisEmailService;
    @Lazy
    @Autowired
    private UserVerificationService userVerificationService;
    @Autowired
    private UsersLoginRepository usersLoginRepository;
    @Autowired
    private UserAvatarRepository userAvatarRepository;

    public UsersInfo CheckUsernameExists(String username) {
        logger.info("Checking if username exists: {}", username);
        try {
            String capitalizedUsername = CapitalizeFirstLetter(username);
            UsersInfo usersInfo = userInfoRepository.findByUsername(capitalizedUsername).orElse(null);
            if (usersInfo != null) {
                logger.info("Username: {}" + usersInfo.getUsername());
                return usersInfo;
            } else {
                logger.info("Username does not exist: {}");
                return null;
            }
        } catch (Exception e) {
            logger.error("Failed to check username: {}", e.getMessage(), e);
        }
        return null;
    }

    public UsersInfo CheckEmailExists(String email) {
        logger.info("Checking if email exists: {}", email);
        try {
            UsersInfo usersInfo = userInfoRepository.findByEmail(email).orElse(null);
            if (usersInfo != null) {
                logger.info("Email: {}" + usersInfo.getEmail());
                return usersInfo;
            } else {
                logger.info("Email does not exist: {}");
                return null;
            }
        } catch (Exception e) {
            logger.error("Failed to check email: {}", e.getMessage(), e);
        }
        return null;
    }

    public UsersInfo GetUserInfoByEmail(String email) {
        logger.info("Getting UsersInfo: {}" + email);
        try {
            UsersInfo usersInfo = userInfoRepository.findByEmail(email).orElse(null);
            if (usersInfo != null) {
                logger.info("UsersInfo: {}" + usersInfo.getUsername());
                UserInfoDTO userInfoDTO = new UserInfoDTO();
                userInfoDTO.setUserId(usersInfo.getId());
                userInfoDTO.setUsername(usersInfo.getUsername());
                userInfoDTO.setEmail(usersInfo.getEmail());
                userInfoDTO.setAvatarUrl(usersInfo.getAvatarUrl());
                userInfoDTO.setCreatedAt(usersInfo.getCreatedAt());
                userInfoDTO.setModifiedAt(usersInfo.getModifiedAt());
                return usersInfo;
            } else {
                logger.info("UserInfo does not exist: {}");
                return null;
            }

        } catch (Exception e) {
            logger.error("Failed to get UsersInfo: {}", e.getMessage(), e);
        }
        return null;
    }

    @Transactional
    public void SaveUserInfo(UserRegisterDTO userRegisterDTO) {
        logger.info("Saving UsersInfo: {}");
        try {
            UsersInfo usersInfo = new UsersInfo();
            usersInfo.setId(userRegisterDTO.getUserId());
            usersInfo.setUsername(userRegisterDTO.getUsername());
            usersInfo.setEmail(userRegisterDTO.getEmail());
            usersInfo.setAvatarUrl("");
            usersInfo.setCreatedAt(userRegisterDTO.getCreatedAt());
            usersInfo.setModifiedAt(userRegisterDTO.getCreatedAt());
            userInfoRepository.save(usersInfo);
        } catch (Exception e) {
            logger.error("Failed to save UsersInfo: {}", e.getMessage(), e);
        }
    }

    public UserInfoVO GetUserInfo(String username) {
        logger.info("Getting UsersInfo: {}" + username);
        try {
            UsersInfo usersInfo = userInfoRepository.findByUsername(username).orElse(null);
            if (usersInfo != null) {
                logger.info("UsersInfo: {}" + usersInfo.getUsername());
                UserInfoDTO userInfoDTO = new UserInfoDTO();
                userInfoDTO.setUserId(usersInfo.getId());
                userInfoDTO.setUsername(usersInfo.getUsername());
                userInfoDTO.setEmail(usersInfo.getEmail());
                userInfoDTO.setAvatarUrl(usersInfo.getAvatarUrl());
                userInfoDTO.setCreatedAt(usersInfo.getCreatedAt());
                userInfoDTO.setModifiedAt(usersInfo.getModifiedAt());
                return TransferToVO(userInfoDTO);
            } else {
                logger.info("UserInfo does not exist: {}");
                return null;
            }

        } catch (Exception e) {
            logger.error("Failed to get UsersInfo: {}", e.getMessage(), e);
        }
        return null;
    }

    public UserInfoVO GetUserInfo(Long userId) {
        logger.info("Getting UsersInfo: {}" + userId);
        try {
            UsersInfo usersInfo = userInfoRepository.findById(userId).orElse(null);
            if (usersInfo != null) {
                logger.info("UsersInfo: {}" + usersInfo.getUsername());
                UserInfoDTO userInfoDTO = new UserInfoDTO();
                userInfoDTO.setUserId(usersInfo.getId());
                userInfoDTO.setUsername(usersInfo.getUsername());
                userInfoDTO.setEmail(usersInfo.getEmail());
                userInfoDTO.setAvatarUrl(usersInfo.getAvatarUrl());
                userInfoDTO.setCreatedAt(usersInfo.getCreatedAt());
                userInfoDTO.setModifiedAt(usersInfo.getModifiedAt());
                return TransferToVO(userInfoDTO);
            } else {
                logger.info("UserInfo does not exist: {}");
                return null;
            }

        } catch (Exception e) {
            logger.error("Failed to get UsersInfo: {}", e.getMessage(), e);
        }
        return null;
    }

    public UserInfoVO TransferToVO(UserInfoDTO userInfoDTO) {
        UserInfoVO userInfoVO = new UserInfoVO();
        userInfoVO.setLongUid(userInfoDTO.getUserId());
        userInfoVO.setUserId(userInfoDTO.getUserId().toString());
        userInfoVO.setUsername(userInfoDTO.getUsername());
        userInfoVO.setEmail(userInfoDTO.getEmail());
        userInfoVO.setAvatarUrl(userInfoDTO.getAvatarUrl());
        userInfoVO.setCreatedAt(userInfoDTO.getCreatedAt());
        userInfoVO.setModifiedAt(userInfoDTO.getModifiedAt());
        return userInfoVO;
    }

    @Transactional
    public boolean UpdateUserAvatar(MultipartFile[] image, Long userId) {
        logger.info("Updating avatar: {}");
        try {
            String avatarURL = "";
            MultipartFile avatar = image[0];
            // Check if the uploaded file is an image and its size is within limit (e.g., 5MB)
            if (avatar.getContentType().startsWith("image/") && avatar.getSize() <= 5 * 1024 * 1024) {
                //create avatar id for avatar
                long avatarId = Snowflake.generateUniqueId();
                //parse avatar data and type
                byte[] avatarData = avatar.getBytes();
                String avatarType = avatar.getContentType();
                //if ("jpeg".equals(imageFormat) || "png".equals(imageFormat) || "gif".equals(imageFormat)) {
                avatarType = avatarType.substring(avatarType.lastIndexOf('/') + 1);
                logger.info("Avatar type: {}", avatarType);
                //create avatar name
                String avatarName = avatarId + "." + avatarType;
                logger.info("AvatarName: {}", avatarName);
                avatarURL = AVATAR_URL + avatarName;
                logger.info("AvatarURL: {}", avatarURL);
                logger.info("Saving PostImage to Tomcat");
                Path Tomcat_imagePath = Paths.get(TOMCAT_AVATAR_PATH, avatarName);
//                Path Nginx_imagePath = Paths.get(NGINX_AVATAR_PATH, avatarName);
                FileOutputStream fos_tomcat = new FileOutputStream(Tomcat_imagePath.toFile());
//                FileOutputStream fos_nginx = new FileOutputStream(Nginx_imagePath.toFile());
                fos_tomcat.write(avatarData);
//                fos_nginx.write(avatarData);
                fos_tomcat.close();
//                fos_nginx.close();
                logger.info("Saved PostImage to Tomcat");
                return SaveUserAvatar(userId, avatarURL);
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    @Transactional
    private boolean SaveUserAvatar(Long userId, String avatarURL) {
        logger.info("Saving avatar: {}");
        try {
            UsersInfo usersInfo = userInfoRepository.findById(userId).orElse(null);
            if (usersInfo != null) {
                usersInfo.setAvatarUrl(avatarURL);
                usersInfo.setModifiedAt(Instant.now());
                return true;
            } else {
                logger.info("User info does not exist: {}");
                return false;
            }
        } catch (Exception e) {
            logger.error("Failed to save UserAvatar: {}", e.getMessage(), e);
        }
        return false;
    }

    @Transactional
    public boolean UpdateUserEmail(String userId, String newEmail) {
        logger.info("Updating email: {}" + userId + "::::::" + newEmail);
        try {
            Long userIdLong = Long.parseLong(userId);
            UsersInfo usersInfo = userInfoRepository.findById(userIdLong).orElse(null);
            if (usersInfo != null) {
                logger.info("UsersInfo: {}" + usersInfo.getUsername());
                logger.info("Old email: {}" + usersInfo.getEmail());
                usersInfo.setEmail(newEmail);
                logger.info("New email: {}" + newEmail);
                usersInfo.setModifiedAt(Instant.now());
                userInfoRepository.save(usersInfo);
                logger.info("Updated email successfully: {}");
                return true;
            } else {
                logger.info("Failed to update email: {}");
                return false;
            }
        } catch (Exception e) {
            logger.error("Failed to update UsersInfo: {}", e.getMessage(), e);
        }
        return false;
    }

    @Transactional
    public boolean UpdateUserPassword(UpdatePasswordDTO updatePasswordDTO) {
        logger.info("Updating password: {}");
        try {
            UsersLogin user = usersLoginRepository.findById(updatePasswordDTO.getUserId()).orElse(null);
            logger.info("Found user: {}" + user.getUsername());
            String oldPassword = user.getPassword();
            if (BCrypt.checkpw(updatePasswordDTO.getOldPassword(), oldPassword)) {
                logger.info("Old password is correct: {}" + updatePasswordDTO.getOldPassword());
                String newPassword = BCrypt.hashpw(updatePasswordDTO.getNewPassword(), BCrypt.gensalt());
                user.setPassword(newPassword);
                user.setModifiedAt(Instant.now());
                usersLoginRepository.save(user);
                logger.info("Updated password successfully: {}");
                return true;
            } else {
                logger.info("Old password is incorrect: {}" + updatePasswordDTO.getOldPassword());
                return false;
            }
        } catch (Exception e) {
            logger.error("Failed to update password: {}", e.getMessage(), e);
        }
        return false;
    }

    @Transactional
    public boolean ResetUserPassword(ForgotPasswordDTO forgotPasswordDTO) {
        logger.info("Resetting password: {}");
        try {
            UsersLogin user = usersLoginRepository.findById(forgotPasswordDTO.getUserId()).orElse(null);
            logger.info("Found user: {}" + user.getUsername());
            String newPassword = BCrypt.hashpw(forgotPasswordDTO.getNewPassword(), BCrypt.gensalt());
            user.setPassword(newPassword);
            user.setModifiedAt(Instant.now());
            usersLoginRepository.save(user);
            return true;
        } catch (Exception e) {
            logger.error("Failed to reset password: {}", e.getMessage(), e);
        }
        return false;
    }
    private String CapitalizeFirstLetter(String username){
        return username.substring(0, 1).toUpperCase() + username.substring(1).toLowerCase();
    }
}

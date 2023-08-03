package com.example.demo.Service.UsersPostsSetting;

import com.example.demo.Mapper.Repository.UsersPostsSettingRepository;
import com.example.demo.Model.DTO.UserRegisterDTO;
import com.example.demo.Model.Entity.UsersPostsSetting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Instant;

@Service
public class UsersPostsSettingService {
    private static final Logger logger = LoggerFactory.getLogger(UsersPostsSettingService.class);
    @Autowired
    private UsersPostsSettingRepository usersPostsSettingRepository;

    @Transactional
    public void SaveSetting(UserRegisterDTO userRegisterDTO) {
        logger.info("Setting up UsersPostsSetting: {}");
        try {
            UsersPostsSetting usersPostsSetting = new UsersPostsSetting();
            usersPostsSetting.setUserId(userRegisterDTO.getUserId());
            usersPostsSetting.setMentionOn(true);
            usersPostsSetting.setCreatedAt(userRegisterDTO.getCreatedAt());
            usersPostsSetting.setModifiedAt(userRegisterDTO.getCreatedAt());
            usersPostsSettingRepository.save(usersPostsSetting);
        } catch (Exception e) {
            logger.error("Failed to set UsersPostsSetting: {}", e.getMessage(),e);
        }
    }

}

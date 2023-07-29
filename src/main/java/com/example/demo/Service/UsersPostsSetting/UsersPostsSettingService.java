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

    @Transactional(rollbackOn = Exception.class)
    public void SaveSetting(UserRegisterDTO userRegisterDTO, String uuId, Instant instant) {
        logger.info("Setting up UsersPostsSetting: {}");
        try {
            UsersPostsSetting usersPostsSetting = new UsersPostsSetting();
            usersPostsSetting.setUserId(uuId);
            usersPostsSetting.setMentionOn(true);
            usersPostsSetting.setCreatedAt(instant);
            usersPostsSetting.setModifiedAt(instant);
            usersPostsSettingRepository.save(usersPostsSetting);
        } catch (Exception e) {
            logger.error("Failed to set UsersPostsSetting", e);
        }
    }

}

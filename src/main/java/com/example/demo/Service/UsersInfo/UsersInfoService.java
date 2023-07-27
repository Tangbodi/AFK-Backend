package com.example.demo.Service.UsersInfo;

import com.example.demo.Mapper.Repository.UsersInfoRepository;
import com.example.demo.Model.DTO.UserRegisterDTO;
import com.example.demo.Model.Entity.UsersInfo;
import com.example.demo.Service.UsersPostsSetting.UsersPostsSettingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class UsersInfoService {
    private static final Logger logger = LoggerFactory.getLogger(UsersInfoService.class);
    @Autowired
    private UsersInfoRepository usersInfoRepository;
    public void SetUsersInfo(UserRegisterDTO userRegisterDTO, String uuId, Instant instant){
        logger.info("Setting up users_info :{}");
        try{
            UsersInfo usersInfo = new UsersInfo();
            usersInfo.setUserId(uuId);
            usersInfo.setUsername(userRegisterDTO.getUsername());
            usersInfo.setEmail(userRegisterDTO.getEmail());
            usersInfo.setCreatedAt(instant);
            usersInfo.setModifiedAt(instant);
            usersInfoRepository.save(usersInfo);
        }catch (Exception e){
            logger.error("Failed to set users_info", e);
        }
    }

}

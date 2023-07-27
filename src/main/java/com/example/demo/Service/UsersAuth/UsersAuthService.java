package com.example.demo.Service.UsersAuth;

import com.example.demo.Mapper.Repository.UsersAuthRepository;
import com.example.demo.Model.DTO.UserRegisterDTO;
import com.example.demo.Model.Entity.UsersAuth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class UsersAuthService {
    private static final Logger logger = LoggerFactory.getLogger(UsersAuthService.class);
    @Autowired
    private UsersAuthRepository usersAuthRepository;
    public void SetUsersAuth(UserRegisterDTO userRegisterDTO, String uuId, Instant instant){
        logger.info("Setting up users_auth :{}");
        try{
            UsersAuth usersAuth = new UsersAuth();
            usersAuth.setUserId(uuId);
            usersAuth.setUsername(userRegisterDTO.getUsername());
            usersAuth.setIsVerified(false);
            usersAuth.setIsBlocked(false);
            usersAuth.setCreatedAt(instant);
            usersAuth.setModifiedAt(instant);
            usersAuthRepository.save(usersAuth);
        }catch (Exception e){
            logger.error("Failed to set users_auth", e);
        }
    }
}

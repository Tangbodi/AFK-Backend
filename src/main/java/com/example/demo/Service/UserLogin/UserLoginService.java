package com.example.demo.Service.UserLogin;

import com.example.demo.Mapper.Repository.UserRepository;
import com.example.demo.Model.DTO.UserLoginDTO;
import com.example.demo.Model.Entity.User;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserLoginService {
    private static final Logger logger = LoggerFactory.getLogger(UserLoginService.class);
    @Autowired
    private UserRepository userRepository;

    public boolean CheckPassword(UserLoginDTO userLoginDTO){
        logger.info("Checking if password correct: {}", userLoginDTO.getUsername());
        try{
            User user = userRepository.findByUsername(userLoginDTO.getUsername()).orElse(null);
            if(user != null){
                logger.info("User exists: {}");
                logger.info("Checking password: {}");
                if(BCrypt.checkpw(userLoginDTO.getPassword(), user.getPassword())){
                    return true;
                } else{
                    return false;
                }
            } else {
                logger.info("User does not exist: {}");
            }
        }catch (Exception e){
            logger.error("Failed to check password: {}", e.getMessage(),e);
        }
        return false;
    }
}

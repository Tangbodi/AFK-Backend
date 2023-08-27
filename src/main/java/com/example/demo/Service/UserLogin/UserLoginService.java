package com.example.demo.Service.UserLogin;

import com.example.demo.Repository.UserRepository;
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

    private final UserRepository userRepository;

    @Autowired
    public UserLoginService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean CheckPassword(UserLoginDTO userLoginDTO) {
        logger.info("Checking if password is correct for user: {}", userLoginDTO.getUsername());
        try {
            User user = userRepository.findByUsername(userLoginDTO.getUsername()).orElse(null);
            if (user != null) {
                logger.info("User exists: {}", user.getUsername());
                logger.info("Checking password");
                if (BCrypt.checkpw(userLoginDTO.getPassword(), user.getPassword())) {
                    logger.info("Password is correct for user: {}", user.getUsername());
                    return true;
                } else {
                    logger.info("Password is incorrect for user: {}", user.getUsername());
                    return false;
                }
            } else {
                logger.info("User does not exist: {}", userLoginDTO.getUsername());
            }
        } catch (Exception e) {
            logger.error("Failed to check password: {}", e.getMessage(), e);
        }
        return false;
    }
}

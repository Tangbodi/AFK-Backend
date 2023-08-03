package com.example.demo.Service.UsersInfo;

import com.example.demo.Mapper.Repository.UsersMailAddressRepository;
import com.example.demo.Model.DTO.UserMailDTO;
import com.example.demo.Model.DTO.UserRegisterDTO;
import com.example.demo.Model.Entity.UsersMailAddress;
import com.example.demo.Model.VO.UserMailAddressVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class UsersMailAddressService {
    private static final Logger logger = LoggerFactory.getLogger(UsersMailAddressService.class);
    @Autowired
    private UsersMailAddressRepository usersMailAddressRepository;
    public void UpdateUserMailAddress(UserMailDTO userMailDTO){
        logger.info("Updating user mail address: {}");
        try{
            UsersMailAddress usersMailAddress = usersMailAddressRepository.findById(userMailDTO.getUserId()).orElse(null);
            if(usersMailAddress != null){
                logger.info("User mail address: {}",usersMailAddress.getId());
                usersMailAddress.setCountry(userMailDTO.getCountry());
                usersMailAddress.setState(userMailDTO.getState());
                usersMailAddress.setAddress(userMailDTO.getAddress());
                usersMailAddress.setCity(userMailDTO.getCity());
                usersMailAddress.setZip(userMailDTO.getZip());
                usersMailAddress.setPhone(userMailDTO.getPhone());
                usersMailAddress.setModifiedAt(Instant.now());
                usersMailAddressRepository.save(usersMailAddress);
                logger.info("Updated user mail address: {}");
            }else{
                logger.info("User mail not found: {}");
                logger.info("Creating new user mail address: {}");
                UsersMailAddress newUsersMailAddress = new UsersMailAddress();
                newUsersMailAddress.setId(userMailDTO.getUserId());
                newUsersMailAddress.setCountry(userMailDTO.getCountry());
                newUsersMailAddress.setState(userMailDTO.getState());
                newUsersMailAddress.setAddress(userMailDTO.getAddress());
                newUsersMailAddress.setCity(userMailDTO.getCity());
                newUsersMailAddress.setZip(userMailDTO.getZip());
                newUsersMailAddress.setPhone(userMailDTO.getPhone());
                newUsersMailAddress.setCreatedAt(Instant.now());
                newUsersMailAddress.setModifiedAt(Instant.now());
                usersMailAddressRepository.save(newUsersMailAddress);
                logger.info("Created new user mail address: {}");
            }
        }catch (Exception e){
            logger.error("Failed to update user mail address: {}",e.getMessage(),e);
        }
    }
    public UserMailAddressVO GetUserMailAddress(String userId){
        logger.info("Getting user mail address: {}");
        try{
            UsersMailAddress usersMailAddress = usersMailAddressRepository.findById(userId).orElse(null);
            if(usersMailAddress != null){
                logger.info("User mail address: {}",usersMailAddress.getId());
                UserMailAddressVO userMailAddressVO = new UserMailAddressVO();
                userMailAddressVO.setCountry(usersMailAddress.getCountry());
                userMailAddressVO.setState(usersMailAddress.getState());
                userMailAddressVO.setAddress(usersMailAddress.getAddress());
                userMailAddressVO.setCity(usersMailAddress.getCity());
                userMailAddressVO.setZip(usersMailAddress.getZip());
                userMailAddressVO.setPhone(usersMailAddress.getPhone());
                return userMailAddressVO;
            }else{
                logger.info("User mail not found: {}");
                return null;
            }
        }catch (Exception e){
            logger.error("Failed to get user mail address: {}",e.getMessage(),e);
        }
        return null;
    }
    public void SetUserMailAddress(UserRegisterDTO userRegisterDTO){
        logger.info("Setting user mail address: {}");
        try{
            UsersMailAddress usersMailAddress = new UsersMailAddress();
            usersMailAddress.setId(userRegisterDTO.getUserId());
            usersMailAddress.setCreatedAt(userRegisterDTO.getCreatedAt());
            usersMailAddress.setModifiedAt(userRegisterDTO.getCreatedAt());
            usersMailAddressRepository.save(usersMailAddress);
            logger.info("Set user mail address: {}");
        }catch (Exception e){
            logger.error("Failed to set user mail address: {}",e.getMessage(),e);
        }

    }

}

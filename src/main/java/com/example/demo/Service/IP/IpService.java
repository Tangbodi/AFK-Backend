package com.example.demo.Service.IP;

import com.example.demo.Mapper.Repository.IpAddressRepository;
import com.example.demo.Model.DTO.IpAddressDTO;
import com.example.demo.Model.DTO.PostDTO;
import com.example.demo.Model.Entity.IpAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.apache.commons.validator.routines.InetAddressValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class IpService {
    private static final Logger logger = LoggerFactory.getLogger(IpService.class);
    private static final InetAddressValidator VALIDATOR = InetAddressValidator.getInstance();
    public boolean isValidInet4Address(String ip){
        try{
            logger.info("Checking if IP is IPV4:::");
            return VALIDATOR.isValidInet4Address(ip);
        }catch (Exception e){
            logger.error("isValidInet4Address:::Exception:::"+e.getMessage(),e);
        }
        return false;
    }
    public boolean isValidInet6Address(String ip){
        try{
            logger.info("Checking if IP is IPV6:::");
            return VALIDATOR.isValidInet6Address(ip);
        }catch (Exception e){
            logger.error("isValidInet6Address:::Exception:::"+e.getMessage(),e);
        }
        return false;
    }

}

package com.example.demo.Service.IP;

import com.example.demo.Mapper.Repository.IpAddressRepository;
import com.example.demo.Model.DTO.IpAddressDTO;
import com.example.demo.Model.Entity.IpAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class IpAddressService {
    private static final Logger logger = LoggerFactory.getLogger(IpAddressService.class);

    @Autowired
    private IpAddressRepository ipAddressRepository;

    @Transactional
    public void SetIpAddress(IpAddressDTO ipAddressDTO) {
        logger.info("Setting IP Address:::");
        try {
            IpAddress ipAddress = new IpAddress();
            ipAddress.setId(ipAddressDTO.getId());
            ipAddress.setIpvFour(ipAddressDTO.getIpvFour());
            ipAddress.setIpvSix(ipAddressDTO.getIpvSix());
            ipAddress.setCreatedAt(ipAddressDTO.getCreatedAt());
            ipAddress.setModifiedAt(ipAddressDTO.getCreatedAt());
            ipAddressRepository.save(ipAddress);
        } catch (Exception e) {
            logger.error("SetIpAddress:::Exception:::" + e.getMessage(), e);
        }
    }
}

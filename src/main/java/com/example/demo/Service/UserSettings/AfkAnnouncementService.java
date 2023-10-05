package com.example.demo.Service.UserSettings;

import com.example.demo.Mapper.Repository.AfkAnnouncementRepository;
import com.example.demo.Model.DTO.UserRegisterDTO;
import com.example.demo.Model.Entity.AfkAnnouncement;
import com.example.demo.Model.Entity.UsersVerificationToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Instant;

@Service
public class AfkAnnouncementService {
    private static final Logger logger = LoggerFactory.getLogger(AfkAnnouncementService.class);
    @Autowired
    private AfkAnnouncementRepository afkAnnouncementRepository;
    @Async("MultiExecutor")
    @Transactional
    public void SaveAfkAnnouncement(UsersVerificationToken usersVerificationToken){
        logger.info("Saving afk announcement");
        try {
            AfkAnnouncement afkAnnouncement = new AfkAnnouncement();
            afkAnnouncement.setId(usersVerificationToken.getId());
            afkAnnouncement.setMentionOn(true);
            afkAnnouncement.setCreatedAt(usersVerificationToken.getModifiedAt());
            afkAnnouncement.setModifiedAt(usersVerificationToken.getModifiedAt());
            afkAnnouncementRepository.save(afkAnnouncement);
            logger.info("Afk announcement saved");
        } catch (Exception e) {
            logger.error("Failed to save afk announcement", e.getMessage(), e);
        }
    }
}

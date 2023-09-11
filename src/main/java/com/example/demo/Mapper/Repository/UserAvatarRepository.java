package com.example.demo.Mapper.Repository;

import com.example.demo.Model.Entity.UserAvatar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserAvatarRepository extends JpaRepository<UserAvatar,Long> {
}

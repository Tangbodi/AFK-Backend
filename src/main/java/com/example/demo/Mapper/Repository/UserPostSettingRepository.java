package com.example.demo.Mapper.Repository;

import com.example.demo.Model.Entity.UsersPostsSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserPostSettingRepository extends JpaRepository<UsersPostsSetting, Long> {
}

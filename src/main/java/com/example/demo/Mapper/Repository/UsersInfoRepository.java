package com.example.demo.Mapper.Repository;

import com.example.demo.Model.Entity.User;
import com.example.demo.Model.Entity.UsersInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsersInfoRepository extends JpaRepository<UsersInfo,String> {
    UsersInfo findByUsername(String username);
    UsersInfo findByEmail(String email);
}

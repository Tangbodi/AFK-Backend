package com.example.demo.Mapper.Repository;

import com.example.demo.Model.Entity.UsersResetPasswordToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserResetPasswordRepository extends JpaRepository<UsersResetPasswordToken, Long> {
}

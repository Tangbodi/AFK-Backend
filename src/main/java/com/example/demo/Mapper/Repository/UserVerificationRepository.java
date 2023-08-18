package com.example.demo.Mapper.Repository;

import com.example.demo.Model.Entity.UsersVerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserVerificationRepository extends JpaRepository<UsersVerificationToken, String> {
    Optional<UsersVerificationToken> findByToken(String token);
}

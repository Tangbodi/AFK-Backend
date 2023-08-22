package com.example.demo.Mapper.Repository;

import com.example.demo.Model.Entity.UsersVerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserVerificationRepository extends JpaRepository<UsersVerificationToken, Long> {
    @Query(value = "SELECT * FROM afk.users_verification_token WHERE token = :token", nativeQuery = true)
    UsersVerificationToken findByToken(@Param("token") String token);

    @Query(value = "SELECT * FROM afk.users_verification_token WHERE username = :username", nativeQuery = true)
    UsersVerificationToken findByUsername(@Param("username") String username);
}

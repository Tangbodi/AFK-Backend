package com.example.demo.Mapper.Repository;

import com.example.demo.Model.Entity.UsersLogin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsersLoginRepository extends JpaRepository<UsersLogin, Long> {
    Optional<UsersLogin> findByUsername(String username);
}

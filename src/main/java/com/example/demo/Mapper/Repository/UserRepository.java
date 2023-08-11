package com.example.demo.Mapper.Repository;

import com.example.demo.Model.Entity.User;
import com.example.demo.Model.Entity.UsersAuth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByUsername(String username);
}

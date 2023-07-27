package com.example.demo.Mapper.Repository;

import com.example.demo.Model.Entity.User;
import com.example.demo.Model.Entity.UsersAuth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsersAuthRepository extends JpaRepository<UsersAuth,String> {
}

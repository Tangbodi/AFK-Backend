package com.example.demo.Repository;

import com.example.demo.Model.Entity.UsersMailAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserMailAddressRepository extends JpaRepository<UsersMailAddress, Long> {
}

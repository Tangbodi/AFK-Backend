package com.example.demo.Mapper.Repository;

import com.example.demo.Model.Entity.UsersMailAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsersMailAddressRepository extends JpaRepository<UsersMailAddress, String> {
}

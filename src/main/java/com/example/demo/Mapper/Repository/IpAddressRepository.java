package com.example.demo.Mapper.Repository;

import com.example.demo.Model.Entity.IpAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IpAddressRepository extends JpaRepository<IpAddress,Long> {

}

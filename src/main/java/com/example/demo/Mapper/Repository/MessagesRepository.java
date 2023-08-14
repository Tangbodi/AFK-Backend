package com.example.demo.Mapper.Repository;

import com.example.demo.Model.Entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessagesRepository extends JpaRepository<Message,Long> {
}

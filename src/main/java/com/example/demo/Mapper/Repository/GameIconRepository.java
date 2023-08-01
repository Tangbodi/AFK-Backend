package com.example.demo.Mapper.Repository;

import com.example.demo.Model.Entity.GameIcon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameIconRepository extends JpaRepository<GameIcon, Long>{
}

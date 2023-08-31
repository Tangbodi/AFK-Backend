package com.example.demo.Mapper.Repository;

import com.example.demo.Model.Entity.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameRepository extends JpaRepository<Game,Short> {
}

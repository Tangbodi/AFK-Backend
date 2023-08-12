package com.example.demo.Mapper.Repository;

import com.example.demo.Model.Entity.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GamesRepository extends JpaRepository<Game,Short> {
}

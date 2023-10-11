package com.example.demo.Mapper.Repository;

import com.example.demo.Model.Entity.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface GameRepository extends JpaRepository<Game,Short> {
    @Query(value = "SELECT game_id FROM afk.games;", nativeQuery = true)
    List<Map<Short,Object>> findAllGameIds();
}

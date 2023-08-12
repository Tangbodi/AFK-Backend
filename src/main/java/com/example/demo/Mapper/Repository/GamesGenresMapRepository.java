package com.example.demo.Mapper.Repository;

import com.example.demo.Model.Entity.GamesGenresMap;
import com.example.demo.Model.Entity.GamesGenresMapId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GamesGenresMapRepository extends JpaRepository<GamesGenresMap, GamesGenresMapId> {
}

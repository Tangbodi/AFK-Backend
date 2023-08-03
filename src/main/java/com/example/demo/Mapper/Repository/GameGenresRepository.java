package com.example.demo.Mapper.Repository;

import com.example.demo.Model.Entity.GameGenre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameGenresRepository extends JpaRepository<GameGenre,Byte> {
}

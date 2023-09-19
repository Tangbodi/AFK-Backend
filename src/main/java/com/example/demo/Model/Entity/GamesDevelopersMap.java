package com.example.demo.Model.Entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

//@Getter
//@Setter
//@Entity
//@Table(name = "games_developers_map")
//public class GamesDevelopersMap {
//    @Id
//    @Column(name = "game_id", nullable = false)
//    private Short id;
//
//    @Size(max = 127)
//    @NotNull
//    @Column(name = "developer", nullable = false, length = 127)
//    private String developer;
//
//}
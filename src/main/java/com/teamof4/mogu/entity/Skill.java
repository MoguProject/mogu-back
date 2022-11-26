package com.teamof4.mogu.entity;

import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter
public class Skill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String skillName;

}
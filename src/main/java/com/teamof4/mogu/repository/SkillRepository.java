package com.teamof4.mogu.repository;

import com.teamof4.mogu.entity.Skill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SkillRepository extends JpaRepository<Skill, Integer> {

    Optional<Skill> findBySkillName(String skillName);
}

package com.teamof4.mogu.repository;

import com.teamof4.mogu.entity.PostSkill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface PostSkillRepository extends JpaRepository<PostSkill, Long> {
}

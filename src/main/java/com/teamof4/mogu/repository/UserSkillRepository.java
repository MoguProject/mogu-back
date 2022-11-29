package com.teamof4.mogu.repository;

import com.teamof4.mogu.entity.UserSkill;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserSkillRepository extends JpaRepository<UserSkill, Long> {

    UserSkill findAllByUserIdAndSkillId(Long userId, Long skillId);
}

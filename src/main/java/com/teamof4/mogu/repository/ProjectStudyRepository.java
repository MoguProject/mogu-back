package com.teamof4.mogu.repository;

import com.teamof4.mogu.entity.Post;
import com.teamof4.mogu.entity.ProjectStudy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface ProjectStudyRepository extends JpaRepository<ProjectStudy, Long> {
    Optional<ProjectStudy> findByPost(Post post);
}

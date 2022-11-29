package com.teamof4.mogu.repository;

import com.teamof4.mogu.entity.Post;
import com.teamof4.mogu.entity.ProjectStudy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface ProjectStudyRepository extends JpaRepository<ProjectStudy, Long> {
    Optional<ProjectStudy> findByPost(Post post);

    Page<ProjectStudy> findAll(Pageable pageable);

    @Query("SELECT ps FROM ProjectStudy ps\n" +
            "join ps.post p WHERE p.title LIKE %:keyword% OR p.content LIKE %:keyword%")
    Page<ProjectStudy> findAllByTitleAndContentContainingIgnoreCase(String keyword, Pageable pageable);
}

package com.teamof4.mogu.repository;

import com.teamof4.mogu.entity.Category;
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

    @Query("SELECT ps FROM ProjectStudy ps join ps.post p " +
            "WHERE p.category = :category " +
            "ORDER BY p.id DESC")
    Page<ProjectStudy> findAll(Category category, Pageable pageable);

    @Query("SELECT ps FROM ProjectStudy ps join ps.post p " +
            "WHERE p.category = :category " +
            "ORDER BY size(p.likes) DESC, p.id DESC")
    Page<ProjectStudy> findAllLikesDesc(Category category, Pageable pageable);

    @Query("SELECT ps FROM ProjectStudy ps join ps.post p " +
            "WHERE p.category = :category " +
            "AND p.title LIKE %:keyword% OR p.content LIKE %:keyword% " +
            "ORDER BY p.id DESC")
    Page<ProjectStudy> findAllByTitleAndContentContainingIgnoreCase(String keyword, Category category, Pageable pageable);
}

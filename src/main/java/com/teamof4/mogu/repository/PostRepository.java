package com.teamof4.mogu.repository;

import com.teamof4.mogu.entity.Category;
import com.teamof4.mogu.entity.Post;
import com.teamof4.mogu.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("SELECT p FROM Post p " +
            "WHERE p.category = :category " +
            "ORDER BY p.id DESC")
    Page<Post> findAll(Pageable pageable, Category category);

    @Query("SELECT p FROM Post p " +
            "WHERE p.category = :category " +
            "ORDER BY size(p.likes) DESC, p.id DESC ")
    Page<Post> findAllLikesDesc(Pageable pageable, Category category);

    @Query("SELECT DISTINCT p FROM Post p " +
            "LEFT JOIN FETCH p.projectStudies " +
            "JOIN FETCH p.user " +
            "JOIN p.likes l " +
            "WHERE l.user = :user "+
            "ORDER BY p.id DESC ")
    List<Post> findPostsILiked(Pageable pageable, User user);

    @Query("SELECT DISTINCT p FROM Post p " +
            "JOIN FETCH p.projectStudies " +
            " ")
    List<Post> findAllProjectsByUser(Pageable pageable, Category category, Long userId);
}
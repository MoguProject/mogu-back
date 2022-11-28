package com.teamof4.mogu.repository;

import com.teamof4.mogu.entity.ImagePost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImagePostRepository extends JpaRepository<ImagePost, Long> {

    List<ImagePost> findAllByPostId(Long id);
}

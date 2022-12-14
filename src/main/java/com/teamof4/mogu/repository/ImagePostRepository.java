package com.teamof4.mogu.repository;

import com.teamof4.mogu.entity.Image;
import com.teamof4.mogu.entity.ImagePost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ImagePostRepository extends JpaRepository<ImagePost, Long> {

    Optional<ImagePost> findByImage(Image image);
}

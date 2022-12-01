package com.teamof4.mogu.repository;

import com.teamof4.mogu.entity.Like;
import com.teamof4.mogu.entity.Post;
import com.teamof4.mogu.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {
    Optional<Like> findByUserAndPost(User user, Post post);

    Optional<Like> findByUser(User user);

    int countByPost(Post post);
}

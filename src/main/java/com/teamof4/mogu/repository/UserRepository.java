package com.teamof4.mogu.repository;

import com.teamof4.mogu.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {

    boolean existByEmail(String email);

    boolean existByNickname(String nickname);

    boolean existByPhone(String phone);
}

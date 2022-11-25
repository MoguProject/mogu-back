package com.teamof4.mogu.security;

import com.teamof4.mogu.entity.User;

public interface TokenService {

    public String create(User user);

    public String validateAndGetUserId(String token);
}

package com.teamof4.mogu.exception.user;

import io.jsonwebtoken.JwtException;

public class TokenNotFoundException extends JwtException {
    public TokenNotFoundException(String message) {
        super(message);
    }
}

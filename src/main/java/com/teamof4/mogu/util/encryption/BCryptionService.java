package com.teamof4.mogu.util.encryption;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class BCryptionService implements EncryptionService {

    @Override
    public String encrypt(String rawPassword) {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

        return bCryptPasswordEncoder.encode(rawPassword);
    }

    @Override
    public boolean isSamePassword(String rawPassword, String encodedPassword) {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

        return bCryptPasswordEncoder.matches(rawPassword, encodedPassword);
    }
}

package com.teamof4.mogu.util.encryption;

public interface EncryptionService {
    public String encrypt(String rawPassword);

    public boolean isSamePassword(String rawPassword, String encodedPassword);
}

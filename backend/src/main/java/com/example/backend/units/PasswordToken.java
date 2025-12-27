package com.example.backend.units;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

// This utility class is used to encrypt passwords during registration (before writing to database)
public class PasswordToken {
    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    // Encrypt: called during registration, encrypts plain text password to cipher text (for writing to database)
    public static String encode(String rawPassword) {
        return encoder.encode(rawPassword);
    }

    // Verify: called during subsequent login, compares plain text password with cipher text in database
    public static boolean matches(String rawPassword, String encodedPassword) {
        return encoder.matches(rawPassword, encodedPassword);
    }
}
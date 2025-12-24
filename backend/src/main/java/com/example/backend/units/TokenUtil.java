package com.example.backend.units;

import java.util.UUID;

public class TokenUtil {

    public static String generateResetToken() {

        return UUID.randomUUID().toString().replace("-", "")
                + UUID.randomUUID().toString().substring(0, 16);
    }
}
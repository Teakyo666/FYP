package com.example.backend.Services.Tools;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

// 这个工具类的作用：注册时加密密码（写入数据库前用）
public class PasswordToken {
    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    // 加密：注册时调用，把明文密码加密成密文（写入数据库）
    public static String encode(String rawPassword) {
        return encoder.encode(rawPassword);
    }

    // 验证：后续登录时调用，比对明文密码和数据库中的密文是否匹配
    public static boolean matches(String rawPassword, String encodedPassword) {
        return encoder.matches(rawPassword, encodedPassword);
    }
}
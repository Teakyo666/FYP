package com.example.backend.units;

import java.security.MessageDigest;

public class FileUploadUtils {

    /**
     * 计算字节数组的 SHA-256 哈希值
     */
    public static byte[] sha256(byte[] data) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            return md.digest(data);
        } catch (Exception e) {
            throw new RuntimeException("计算 SHA-256 失败", e);
        }
    }

    /**
     * 将 byte[] 转换为十六进制字符串（小写）
     */
    public static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
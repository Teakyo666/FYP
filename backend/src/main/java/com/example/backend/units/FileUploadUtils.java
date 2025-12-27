package com.example.backend.units;

import java.security.MessageDigest;

public class FileUploadUtils {

    /**
     * Calculate SHA-256 hash value of byte array
     */
    public static byte[] sha256(byte[] data) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            return md.digest(data);
        } catch (Exception e) {
            throw new RuntimeException("SHA-256 calculation failed", e);
        }
    }

    /**
     * Convert byte[] to hexadecimal string (lowercase)
     */
    public static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
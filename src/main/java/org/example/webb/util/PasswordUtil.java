package org.example.webb.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class PasswordUtil {
    private static final String PASSWORD_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz" +
            "0123456789!@#$%^&*()_+";
    private static final int PASSWORD_LENGTH = 16;
    private static final int SALT_LENGTH = 16;

    public static String generateStrongPassword() {
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder(PASSWORD_LENGTH);
        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            password.append(PASSWORD_CHARS.charAt(random.nextInt(PASSWORD_CHARS.length())));
        }
        return password.toString();
    }

    public static String hashPassword(String password, String salt) {
        try {
            String saltedPassword = password + salt; // Добавляем соль к паролю
            MessageDigest md = MessageDigest.getInstance("SHA-256"); // Выбираем алгоритм хеширования (SHA-256)
            byte[] hash = md.digest(saltedPassword.getBytes(StandardCharsets.UTF_8)); // Хешируем пароль с солью

            return Base64.getEncoder().encodeToString(hash); // Кодируем хеш в Base64 для удобства хранения
        } catch (NoSuchAlgorithmException e) {
            System.err.println("Алгоритм хеширования не найден: " + e.getMessage());
            return null;
        }
    }

    public static boolean verifyPassword(String password, String hashedPassword, String salt) {
        String newHash = hashPassword(password, salt); // Хешируем введенный пароль с той же солью
        return newHash != null && newHash.equals(hashedPassword); // Сравниваем полученный хеш с сохраненным хешем
    }

    public static String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt); // Кодируем соль в Base64 для удобства хранения
    }
}

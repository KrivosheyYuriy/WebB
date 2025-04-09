package org.example.webb.util;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.Base64;


public class CSRFTokenUtil {
    private static final String CSRF_TOKEN_PARAM = "csrfToken";
    // Метод для генерации случайного CSRF-токена
    public static String generateCsrfToken() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    public static boolean checkCSRFToken(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        String csrfToken = req.getParameter(CSRF_TOKEN_PARAM);
        return csrfToken != null && csrfToken.equals(session.getAttribute("csrfToken"));
    }
}

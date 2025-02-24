package org.example.webb.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class CookieUtil {
    public static void setCookie(HttpServletResponse resp, String name, String value, int maxAge, String path) {
        if (value != null) {
            String encodedValue = URLEncoder.encode(value, StandardCharsets.UTF_8);
            Cookie cookie = new Cookie(name, encodedValue);
            cookie.setMaxAge(maxAge);
            cookie.setPath(path);
            resp.addCookie(cookie);
        } else {
            // Удаление cookie, если значение null
            Cookie cookie = new Cookie(name, "");
            cookie.setValue(null); // Явное указание null значения
            cookie.setMaxAge(0);
            cookie.setPath(path);
            resp.addCookie(cookie);
        }
    }

    public static String getCookieValue(HttpServletRequest req, String cookieName) {
        Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(cookieName)) {
                    return URLDecoder.decode(cookie.getValue(), StandardCharsets.UTF_8);
                }
            }
        }
        return null;
    }

    public static void setArrayCookie(HttpServletResponse response, String name,
                                      String[] array, int maxAge, String path) {
        if (array != null && array.length > 0) {
            String delimiter = ",";
            String encodedValue = URLEncoder.encode(String.join(delimiter, array), StandardCharsets.UTF_8);
            Cookie cookie = new Cookie(name, encodedValue);
            cookie.setMaxAge(maxAge);
            cookie.setPath(path);
            response.addCookie(cookie);
        }
    }

    public static String[] getArrayCookie(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(name)) {
                    String encodedValue = cookie.getValue();
                    if (encodedValue != null && !encodedValue.isEmpty()) {
                        String decodedValue = java.net.URLDecoder.decode(encodedValue, StandardCharsets.UTF_8);
                        return decodedValue.split(",");
                    }
                }
            }
        }
        return null;
    }

    public static String getAndRemoveCookie(HttpServletRequest req, HttpServletResponse resp, String cookieName) {
        String value = getCookieValue(req, cookieName);
        if (value != null) {
            Cookie cookie = new Cookie(cookieName, "");
            cookie.setValue(null); // Явное указание null значения
            cookie.setMaxAge(0); // Удаление cookie
            cookie.setPath(req.getContextPath());
            resp.addCookie(cookie);
        }
        return value;
    }

    public static void saveValuesToCookies(HttpServletResponse response, Map<String, String> data,
                                           int maxAge, String path) {
        if (data != null) {
            for (Map.Entry<String, String> entry : data.entrySet()) {
                String name = entry.getKey();
                String value = entry.getValue();
                setCookie(response, name, value, maxAge, path);
            }
        }
    }
}
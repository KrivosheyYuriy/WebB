package org.example.webb.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.example.webb.entity.User;
import org.example.webb.repository.AdminRepository;
import org.example.webb.repository.UserRepository;
import org.example.webb.repository.impl.AdminRepositoryImpl;
import org.example.webb.repository.impl.UserRepositoryImpl;
import org.example.webb.util.PasswordUtil;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.Base64;

@WebServlet(name = "loginServlet", value = "/login")
public class LoginServlet extends HttpServlet { // Добавлено объявление класса AuthServlet
    private static final String CSRF_TOKEN_PARAM = "csrfToken"; // Название параметра для CSRF-токена
    private static final String LOGIN_PAGE = "/pages/login.jsp";
    private UserRepository userRepository;
    private AdminRepository adminRepository;

    @Override
    public void init() {
        adminRepository = new AdminRepositoryImpl();
        userRepository = new UserRepositoryImpl();
    }

    // Метод для генерации случайного CSRF-токена
    private static String generateCsrfToken() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session != null && session.getAttribute("user") != null) {
            // Пользователь уже залогинен
            response.sendRedirect("/");
        }
        else {
            // Пользователь не залогинен, генерируем CSRF токен
            HttpSession session2 = request.getSession();

            String csrfToken = generateCsrfToken(); // Генерация CSRF токена
            System.out.println(csrfToken);
            session2.setAttribute(CSRF_TOKEN_PARAM, csrfToken); // Сохранение в сессии

            request.getRequestDispatcher(LOGIN_PAGE).forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String csrfToken = request.getParameter(CSRF_TOKEN_PARAM);

        HttpSession session = request.getSession(false); // Получаем сессию, не создавая новую

        if (session == null || csrfToken == null || !csrfToken.equals(session.getAttribute("csrfToken"))){
            // CSRF-токен отсутствует или не совпадает - возможная CSRF-атака
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "CSRF token invalid"); // Отклоняем запрос
            return;
        }

        // Удаляем CSRF-токен из сессии после проверки
        session.removeAttribute(CSRF_TOKEN_PARAM);

        // Проверяем учетные данные (замените на реальную проверку)
        if (isValidUser(username, password)) {
            // Аутентификация прошла успешно
            session = request.getSession(); // Получаем сессию
            session.setAttribute("user", username); // Сохраняем имя пользователя в сессии
            response.sendRedirect("/"); // Перенаправляем на домашнюю страницу
        } else {
            // Неверные учетные данные
            request.setAttribute("errorMessage", "Неверный логин или пароль"); // Добавляем сообщение об ошибке

            //Перегенерация CSRF токена
            String newCsrfToken = generateCsrfToken(); // Генерация CSRF токена
            session.setAttribute(CSRF_TOKEN_PARAM, newCsrfToken); // Сохранение в сессии

            response.sendRedirect("/login"); // Отображаем форму логина с сообщением об ошибке
        }
    }

    // Вспомогательный метод для проверки учетных данные (замените на реальную проверку)
    private boolean isValidUser(String username, String password) {
        User user = userRepository.findByUsername(username);
        if (user == null)
            return false;

        return PasswordUtil.verifyPassword(password, user.getPasswordHash(), user.getSalt());
    }
}
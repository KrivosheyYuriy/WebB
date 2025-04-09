package org.example.webb.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.example.webb.entity.Admin;
import org.example.webb.entity.User;
import org.example.webb.repository.AdminRepository;
import org.example.webb.repository.UserRepository;
import org.example.webb.repository.impl.AdminRepositoryImpl;
import org.example.webb.repository.impl.UserRepositoryImpl;
import org.example.webb.util.CSRFTokenUtil;
import org.example.webb.util.PasswordUtil;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.Base64;

import static org.example.webb.util.CSRFTokenUtil.generateCsrfToken;

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
            session2.setAttribute(CSRF_TOKEN_PARAM, csrfToken); // Сохранение в сессии
            request.getRequestDispatcher(LOGIN_PAGE).forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        HttpSession session = request.getSession(false); // Получаем сессию, не создавая новую

        if (!CSRFTokenUtil.checkCSRFToken(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "CSRF token invalid"); // Отклоняем запрос
            return;
        }

        // Удаляем CSRF-токен из сессии после проверки
        session.removeAttribute(CSRF_TOKEN_PARAM);

        // Проверяем учетные данные (замените на реальную проверку)
        if (isValidUser(username, password) || isValidAdmin(username, password)) {
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

    private boolean isValidAdmin(String username, String password) {
        Admin admin = adminRepository.findByUsername(username);
        if (admin == null)
            return false;

        return PasswordUtil.verifyPassword(password, admin.getPasswordHash(), admin.getSalt());
    }
}
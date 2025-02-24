package org.example.webb.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.example.webb.dto.PollAnswerDTO;
import org.example.webb.repository.LanguageRepository;
import org.example.webb.repository.PollAnswersRepository;
import org.example.webb.repository.impl.LanguageRepositoryImpl;
import org.example.webb.repository.impl.PollAnswersRepositoryImpl;
import org.example.webb.service.PollService;
import org.example.webb.util.CookieUtil;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

import static org.example.webb.util.CookieUtil.getAndRemoveCookie;
import static org.example.webb.util.CookieUtil.getCookieValue;

@WebServlet(name = "helloServlet", value = "/pages/form")
public class FormServlet extends HttpServlet {

    private Validator validator;
    private PollService pollService;
    private PollAnswersRepository pollAnswersRepository;
    private LanguageRepository languageRepository;

    @Override
    public void init() throws ServletException {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        pollAnswersRepository = new PollAnswersRepositoryImpl(); // или другой способ создания репозитория
        languageRepository = new LanguageRepositoryImpl();
        pollService = new PollService(pollAnswersRepository, languageRepository); // Передаем репозитории
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Восстановление значений полей из cookie
        req.setAttribute("name", getCookieValue(req, "name"));
        req.setAttribute("phone", getCookieValue(req, "phone"));
        req.setAttribute("email", getCookieValue(req, "email"));
        req.setAttribute("biography", getCookieValue(req, "biography"));
        req.setAttribute("birthday", getCookieValue(req, "birthday"));
        req.setAttribute("gender", getCookieValue(req, "gender"));
        req.setAttribute("language", getCookieValue(req, "language"));

        // Восстановление сообщения об ошибке из cookie
        String errorMessage = getAndRemoveCookie(req, resp, "errorMessage");
        if (errorMessage != null) {
            req.setAttribute("errorMessage", errorMessage);
        }

        req.getRequestDispatcher("/pages/form.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Map<String, String> formParams = new HashMap<>();
        formParams.put("language", ", ".concat(String.join(", ",
                req.getParameterValues("language")).concat(", ")));
        formParams.put("birthday", req.getParameter("birthday"));
        String name = req.getParameter("name");
        formParams.put("name", name);
        String phone = req.getParameter("phone");
        formParams.put("phone", phone);
        String email = req.getParameter("email");
        formParams.put("email", email);
        String biography = req.getParameter("biography");
        formParams.put("biography", biography);
        String agreement = req.getParameter("agreement");
        formParams.put("agreement", agreement);
        LocalDate birthday;
        String gender = req.getParameter("gender").toLowerCase();
        formParams.put("gender", gender);
        List<Long> languages;

        // обработка id языков
        try {
            languages = Arrays.stream(req.getParameterValues("language")).
                    map(Long::parseLong).toList();
        }
        catch (Exception e) {
            saveErrorMessageToCookie(req, resp);
            req.getRequestDispatcher("/pages/form").forward(req, resp);
            return;
        }

        // обработка даты рождения
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            birthday = dateFormat.parse(req.getParameter("birthday")).
                    toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        } catch (Exception e) {
            // Обработка ошибки парсинга даты
            req.setAttribute("errorMessage", "Некорректный формат даты рождения");
            req.getRequestDispatcher("/pages/form").forward(req, resp);
            return;
        }

      //  Обрабатываем чекбокс
        if (agreement == null || !agreement.equalsIgnoreCase("on")) {
            req.setAttribute("errorMessage", "Не подтверждено ознакомление с контрактом");
            req.getRequestDispatcher("/pages/form").forward(req, resp);
            return;
        }

        // составление dto и его валидация
        PollAnswerDTO formDto = new PollAnswerDTO(name, phone, email, birthday,
                gender, languages, biography);
        Set<ConstraintViolation<PollAnswerDTO>> violations = validator.validate(formDto);
        if (!violations.isEmpty()) {
            StringBuilder errorMessage = new StringBuilder();
            for (ConstraintViolation<PollAnswerDTO> violation : violations) {
                errorMessage.append(violation.getMessage()).append("<br>");
            }

            req.setAttribute("errorMessage", errorMessage);
            req.getRequestDispatcher("/pages/form").forward(req, resp);
        } else {
            // Валидация пройдена
            pollService.addPoll(formDto); //  Передаем DTO в сервис
            saveSuccessValuesToCookies(req, resp, formParams);
            resp.getWriter().println("Form submitted successfully!");
            resp.sendRedirect(req.getContextPath() + "/");
        }
    }

    private void saveErrorMessageToCookie(HttpServletRequest req, HttpServletResponse resp) {
        String encodedErrorMessage = URLEncoder.encode("errorMessage", StandardCharsets.UTF_8);
        Cookie errorCookie = new Cookie("errorMessage", encodedErrorMessage);
        errorCookie.setMaxAge(-1); // Сессионный cookie
        errorCookie.setPath(req.getContextPath());
        resp.addCookie(errorCookie);
    }

    private void saveSuccessValuesToCookies(HttpServletRequest req, HttpServletResponse resp,
                                            Map<String, String> formParams) {
        int oneYear = 60 * 60 * 24 * 365;
        CookieUtil.saveValuesToCookies(resp, formParams, oneYear, req.getContextPath());
    }
}
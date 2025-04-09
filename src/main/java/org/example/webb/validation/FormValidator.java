package org.example.webb.validation;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.example.webb.dto.PollAnswerDTO;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

import static org.example.webb.util.CookieUtil.saveErrorMessageToCookie;

public class FormValidator {
    public static boolean validateForm(HttpServletRequest req, HttpServletResponse resp, Validator validator) throws ServletException, IOException {
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
        } catch (Exception e) {
            saveErrorMessageToCookie(req, resp);
            req.getRequestDispatcher("/form").forward(req, resp);
            return false;
        }

        // обработка даты рождения
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            birthday = dateFormat.parse(req.getParameter("birthday")).
                    toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        } catch (Exception e) {
            // Обработка ошибки парсинга даты
            req.setAttribute("errorMessage", "Некорректный формат даты рождения");
            req.getRequestDispatcher("/form").forward(req, resp);
            return false;
        }

        //  Обрабатываем чекбокс
        if (agreement == null || !agreement.equalsIgnoreCase("on")) {
            req.setAttribute("errorMessage", "Не подтверждено ознакомление с контрактом");
            req.getRequestDispatcher("/form").forward(req, resp);
            return false;
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
            req.getRequestDispatcher("/form").forward(req, resp);
            return true;
        }
        return false;
    }
}

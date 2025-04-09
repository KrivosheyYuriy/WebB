package org.example.webb.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.webb.entity.PollAnswer;

import java.util.stream.Collectors;

import static org.example.webb.util.CookieUtil.getAndRemoveCookie;
import static org.example.webb.util.CookieUtil.getCookieValue;

public class RequestUtil {
    public static void setValuesOfAnswer(HttpServletRequest req, PollAnswer pollAnswer) {
        req.setAttribute("name", pollAnswer.getUsername());
        req.setAttribute("phone", pollAnswer.getPhoneNumber());
        req.setAttribute("email", pollAnswer.getEmail());
        req.setAttribute("biography", pollAnswer.getBiography());
        req.setAttribute("birthday", pollAnswer.getBirthday());
        req.setAttribute("gender", pollAnswer.getGender());
        req.setAttribute("language", ", ".concat(pollAnswer.getPollAnswersLanguages().
                stream().
                map(answ -> answ.getLanguage().getId().toString()).
                collect(Collectors.joining(", "))
        ).concat(", "));
    }

    public static void setValuesOfCookies(HttpServletRequest req, HttpServletResponse resp) {
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
        if (errorMessage != null)
            req.setAttribute("errorMessage", errorMessage);
    }
}

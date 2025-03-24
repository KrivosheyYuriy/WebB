package org.example.webb.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.example.webb.dto.PollAnswerDTO;
import org.example.webb.entity.PollAnswer;
import org.example.webb.entity.User;
import org.example.webb.repository.LanguageRepository;
import org.example.webb.repository.PollAnswersRepository;
import org.example.webb.repository.UserRepository;
import org.example.webb.repository.impl.LanguageRepositoryImpl;
import org.example.webb.repository.impl.PollAnswersRepositoryImpl;
import org.example.webb.repository.impl.UserRepositoryImpl;
import org.example.webb.service.PollService;
import org.example.webb.service.UserService;
import org.example.webb.util.CookieUtil;
import org.example.webb.util.PasswordUtil;
import org.hibernate.Session;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

import static org.example.webb.util.CookieUtil.getAndRemoveCookie;
import static org.example.webb.util.CookieUtil.getCookieValue;

@WebServlet(name = "formServlet", value = "/form")
public class FormServlet extends HttpServlet {

    private Validator validator;
    private PollService pollService;
    private UserService userService;
    private UserRepository userRepository;
    private PollAnswersRepository pollAnswersRepository;
    private LanguageRepository languageRepository;

    @Override
    public void init() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        pollAnswersRepository = new PollAnswersRepositoryImpl(); // или другой способ создания репозитория
        languageRepository = new LanguageRepositoryImpl();
        userRepository = new UserRepositoryImpl();
        pollService = new PollService(pollAnswersRepository, languageRepository); // Передаем репозитории
        userService = new UserService(userRepository);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        System.out.println(session);
        if (session != null && session.getAttribute("user") != null) {
            String username = (String) session.getAttribute("user");
//            System.out.println(username);
            User user = userRepository.findByUsername(username);
            if (user != null) { // авторизован
                PollAnswer pollAnswer = user.getPollAnswer();
                setValuesOfAnswer(req, pollAnswer);
                req.getRequestDispatcher("/pages/form.jsp").forward(req, resp);
                return;
            }
        }
        setValuesOfCookies(req, resp);

        req.getRequestDispatcher("/pages/form.jsp").forward(req, resp);
    }

    private void setValuesOfCookies(HttpServletRequest req, HttpServletResponse resp) {
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

    private void setValuesOfAnswer(HttpServletRequest req, PollAnswer pollAnswer) {
        req.setAttribute("name", pollAnswer.getUsername());
        req.setAttribute("phone", pollAnswer.getPhoneNumber());
        req.setAttribute("email", pollAnswer.getEmail());
        req.setAttribute("biography", pollAnswer.getBiography());
        req.setAttribute("birthday", pollAnswer.getBirthday());
        req.setAttribute("gender", pollAnswer.getGender());
        System.out.println(pollAnswer.getGender());
        req.setAttribute("language", ", ".concat(pollAnswer.getPollAnswersLanguages().
                stream().
                map(answ -> answ.getLanguage().getId().toString()).
                collect(Collectors.joining(", "))
        ).concat(", "));
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
        } catch (Exception e) {
            saveErrorMessageToCookie(req, resp);
            req.getRequestDispatcher("/form").forward(req, resp);
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
            req.getRequestDispatcher("/form").forward(req, resp);
            return;
        }

        //  Обрабатываем чекбокс
        if (agreement == null || !agreement.equalsIgnoreCase("on")) {
            req.setAttribute("errorMessage", "Не подтверждено ознакомление с контрактом");
            req.getRequestDispatcher("/form").forward(req, resp);
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
            req.getRequestDispatcher("/form").forward(req, resp);
        } else {
            HttpSession session = req.getSession();
            if (session != null && session.getAttribute("user") != null) {
                System.out.println("OK");
                User user = userRepository.findByUsername(session.getAttribute("user").toString());
                PollAnswer pollAnswer = user.getPollAnswer();
                pollService.updatePoll(formDto, pollAnswer);
                saveSuccessValuesToCookies(req, resp, formParams);
                req.getRequestDispatcher("/").forward(req, resp);
                return;
            }
            // Валидация пройдена
            PollAnswer answer = pollService.addPoll(formDto); //  Передаем DTO в сервис

            String password = PasswordUtil.generateStrongPassword();
            User user = userService.addUser(password);
            user.setPollAnswer(answer);
            answer.setUser(user);
            userRepository.merge(user);

            req.setAttribute("generatedLogin", user.getUsername());
            req.setAttribute("generatedPassword", password);

            saveSuccessValuesToCookies(req, resp, formParams);
            req.getRequestDispatcher("/pages/formSuccess.jsp").forward(req, resp);
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
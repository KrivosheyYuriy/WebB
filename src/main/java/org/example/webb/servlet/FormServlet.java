package org.example.webb.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.example.webb.dto.PollAnswerDTO;
import org.example.webb.entity.PollAnswer;
import org.example.webb.entity.User;
import org.example.webb.repository.LanguageRepository;
import org.example.webb.repository.PollAnswerLanguageRepository;
import org.example.webb.repository.PollAnswersRepository;
import org.example.webb.repository.UserRepository;
import org.example.webb.repository.impl.LanguageRepositoryImpl;
import org.example.webb.repository.impl.PollAnswerLanguageRepositoryImpl;
import org.example.webb.repository.impl.PollAnswersRepositoryImpl;
import org.example.webb.repository.impl.UserRepositoryImpl;
import org.example.webb.service.PollService;
import org.example.webb.service.UserService;
import org.example.webb.util.PasswordUtil;
import org.example.webb.util.RequestUtil;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

import static org.example.webb.util.CookieUtil.saveErrorMessageToCookie;
import static org.example.webb.util.CookieUtil.saveSuccessValuesToCookies;

@WebServlet(name = "formServlet", value = "/form")
public class FormServlet extends HttpServlet {

    private Validator validator;
    private PollService pollService;
    private UserService userService;
    private UserRepository userRepository;
    private PollAnswersRepository pollAnswersRepository;
    private LanguageRepository languageRepository;
    private PollAnswerLanguageRepository pollAnswerLanguageRepository;

    @Override
    public void init() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        pollAnswersRepository = new PollAnswersRepositoryImpl(); // или другой способ создания репозитория
        languageRepository = new LanguageRepositoryImpl();
        userRepository = new UserRepositoryImpl();
        pollAnswerLanguageRepository = new PollAnswerLanguageRepositoryImpl();
        pollService = new PollService(pollAnswersRepository, languageRepository, pollAnswerLanguageRepository); // Передаем репозитории
        userService = new UserService(userRepository);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("user") != null) {
            String username = (String) session.getAttribute("user");
            User user = userRepository.findByUsername(username);
            if (user != null) { // авторизован
                PollAnswer pollAnswer = user.getPollAnswer();
                RequestUtil.setValuesOfAnswer(req, pollAnswer);
                req.getRequestDispatcher("/pages/form.jsp").forward(req, resp);
                return;
            }
        }
        RequestUtil.setValuesOfCookies(req, resp);

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
}
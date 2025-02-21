package org.example.webb.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
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

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;

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
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String name = req.getParameter("name");
        String phone = req.getParameter("phone");
        String email = req.getParameter("email");
        LocalDate birthday;
        String gender = req.getParameter("gender").toLowerCase();
        List<Long> languages = Arrays.stream(req.getParameterValues("language")).
                map(Long::parseLong).toList();
        String biography = req.getParameter("biography");
        String agreement = req.getParameter("agreement");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date birthdayUtil = dateFormat.parse(req.getParameter("birthday"));
            if (birthdayUtil.after(new Date())) {
                throw new ParseException("The birth date cannot be in the future", 0);
            }
            birthday = birthdayUtil.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(); // Преобразуем в LocalDate
        } catch (ParseException e) {
            // Обработка ошибки парсинга даты
            req.setAttribute("errorMessage", "Некорректный формат даты рождения");
            req.getRequestDispatcher("/pages/form.jsp").forward(req, resp);
            return;
        }
      //  Обрабатываем чекбокс
        if (!agreement.equalsIgnoreCase("on")) {
            req.setAttribute("errorMessage", "Не подтвердили ознакомление с контрактом");
            req.getRequestDispatcher("/pages/form.jsp").forward(req, resp);
            return;
        }

        // Проверка, что все ID языков есть в базе данных
        for (Long languageId : languages) {
            if (languageRepository.findById(languageId) == null) {
                req.setAttribute("errorMessage", "Язык с ID " + languageId + " не найден");
                req.getRequestDispatcher("/pages/form.jsp").forward(req, resp);
                return;
            }
        }

        PollAnswerDTO formDto = new PollAnswerDTO(name, phone, email, birthday,
                gender, languages, biography, LocalDateTime.now());
        Set<ConstraintViolation<PollAnswerDTO>> violations = validator.validate(formDto);

        if (!violations.isEmpty()) {
            StringBuilder errorMessage = new StringBuilder();
            for (ConstraintViolation<PollAnswerDTO> violation : violations) {
                errorMessage.append(violation.getMessage()).append("<br>");
            }

            req.setAttribute("errorMessage", errorMessage.toString());
            req.getRequestDispatcher("/pages/form.jsp").forward(req, resp);
        } else {
            // Валидация пройдена
            pollService.addPoll(formDto); //  Передаем DTO в сервис
            resp.getWriter().println("Form submitted successfully!");
        }
    }
}
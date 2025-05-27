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
import org.example.webb.entity.Admin;
import org.example.webb.entity.PollAnswer;
import org.example.webb.repository.AdminRepository;
import org.example.webb.repository.LanguageRepository;
import org.example.webb.repository.PollAnswerLanguageRepository;
import org.example.webb.repository.PollAnswersRepository;
import org.example.webb.repository.impl.AdminRepositoryImpl;
import org.example.webb.repository.impl.LanguageRepositoryImpl;
import org.example.webb.repository.impl.PollAnswerLanguageRepositoryImpl;
import org.example.webb.repository.impl.PollAnswersRepositoryImpl;
import org.example.webb.service.PollService;
import org.example.webb.util.CSRFTokenUtil;
import org.example.webb.util.RequestUtil;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

import static org.example.webb.util.CSRFTokenUtil.generateCsrfToken;
import static org.example.webb.util.CookieUtil.saveErrorMessageToCookie;
import static org.example.webb.util.CookieUtil.saveSuccessValuesToCookies;

@WebServlet(name = "adminServlet", value = "/admin/*")
public class AdminServlet extends HttpServlet {
    private static final String CSRF_TOKEN_PARAM = "csrfToken";
    private PollService pollService;
    private Validator validator;
    private AdminRepository adminRepository;
    private PollAnswersRepository pollAnswersRepository;
    private PollAnswerLanguageRepository pollAnswerLanguageRepository;

    @Override
    public void init() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        pollAnswersRepository = new PollAnswersRepositoryImpl();
        adminRepository = new AdminRepositoryImpl();
        LanguageRepository languageRepository = new LanguageRepositoryImpl();
        pollAnswerLanguageRepository = new PollAnswerLanguageRepositoryImpl();
        pollService = new PollService(pollAnswersRepository, languageRepository, pollAnswerLanguageRepository);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        if (!checkAdmin(request)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().println("unauthorized");
            return;
        }
        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println("path is missing");
            return;
        } else if (pathInfo.equals("/langstat")) {
            List<Object[]> statistics = pollAnswerLanguageRepository.countLanguages();

            // Устанавливаем статистику как атрибут запроса, чтобы JSP могла получить к ней доступ
            request.setAttribute("languageStatistics", statistics);

            // Перенаправляем запрос на JSP страницуpublic
            request.getRequestDispatcher("/pages/langstat.jsp").forward(request, response);
            return;
        }
        try {
            List<String> args = List.of(pathInfo.substring(1).split("/"));
            Long id = Long.parseLong(args.get(1));
            if (args.get(0).equals("form")) {
                PollAnswer answer = pollAnswersRepository.findById(id);
                if (answer == null) {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    response.getWriter().println("answer not found");
                    return;
                }
                HttpSession session2 = request.getSession();
                String csrfToken = generateCsrfToken(); // Генерация CSRF токена
                session2.setAttribute(CSRF_TOKEN_PARAM, csrfToken); // Сохранение в сессии

                RequestUtil.setValuesOfAnswer(request, answer);
                request.setAttribute("formId", id);
                request.getRequestDispatcher("/pages/adminForm.jsp").forward(request, response);
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println("Invalid ID format");
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println("Internal server error");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!CSRFTokenUtil.checkCSRFToken(req)) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "CSRF token invalid"); // Отклоняем запрос
            return;
        }

        if (!checkAdmin(req)) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().println("unauthorized");
            return;
        }

        try {
            String pathInfo = req.getPathInfo();
            if (pathInfo == null || pathInfo.equals("/")) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().println("ID is missing");
                return;
            }

            List<String> args = List.of(pathInfo.substring(1).split("/"));
            Long id = Long.parseLong(args.get(1));
            if (args.get(0).equals("form")) {

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
                    PollAnswer pollAnswer = pollAnswersRepository.findById(id);
                    pollService.updatePoll(formDto, pollAnswer);
                    saveSuccessValuesToCookies(req, resp, formParams);
                    resp.sendRedirect("/admin/langstat");
                }
            }
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().println("Invalid ID format");
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().println("Internal server error");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (!checkAdmin(request)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().println("unauthorized");
            return;
        }
        try {
            // 1. Получить ID из URL
            String pathInfo = request.getPathInfo();
            if (pathInfo == null || pathInfo.equals("/")) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().println("ID is missing");
                return;
            }

            List<String> args = List.of(pathInfo.substring(1).split("/"));

            if (args.size() != 2 || !args.get(0).equals("form")) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().println("Invalid URL format. Expected /form/{id}");
                return;
            }

            Long id = Long.parseLong(args.get(1));

            // 2. Удалить данные из базы данных
            PollAnswer answer = pollAnswersRepository.findById(id);

            if (answer == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().println("Form not found with ID: " + id);
                return;
            }

            pollAnswersRepository.deleteById(id);
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().println("Form deleted successfully.");

        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println("Invalid ID format");
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println("Internal server error: " + e.getMessage());
        }
    }

    private boolean checkAdmin(HttpServletRequest req)  {
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("user") != null) {
            String username = (String) session.getAttribute("user");
            Admin admin = adminRepository.findByUsername(username);
            return admin != null;
        }
        return false;
    }
}
package org.example.webb.servlet;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import org.example.webb.entity.PollAnswer;
import org.example.webb.repository.PollAnswersRepository;
import org.example.webb.repository.impl.PollAnswersRepositoryImpl;

import java.io.IOException;

@WebServlet(name = "adminServlet", value = "/admin/*")
public class AdminServlet extends HttpServlet {
    private PollAnswersRepository pollAnswersRepository;

    @Override
    public void init() {
        pollAnswersRepository = new PollAnswersRepositoryImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {

    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) {

    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            // 1. Получить ID из URL
            String pathInfo = request.getPathInfo();
            if (pathInfo == null || pathInfo.equals("/")) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().println("ID is missing");
                return;
            }
            String idString = pathInfo.substring(1); // Убрать первый '/'
            long id = Long.parseLong(idString);

            // 2. Удалить данные из базы данных
            PollAnswer answer = pollAnswersRepository.findById(id);
            if (answer == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
            else {
                pollAnswersRepository.deleteById(id);
                response.setStatus(HttpServletResponse.SC_OK);
                response.sendRedirect("/admin");
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println("Invalid ID format");
        } catch (Exception e) {
            // Логирование ошибки
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println("Internal server error");
        }
    }
}

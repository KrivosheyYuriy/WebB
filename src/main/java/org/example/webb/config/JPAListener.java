package org.example.webb.config;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class JPAListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("JPA EntityManagerFactory Initializing");
        JPAConfiguration.getEntityManagerFactory(); // Инициализация EntityManagerFactory
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("JPA EntityManagerFactory Closing");
        JPAConfiguration.shutdown(); // Закрытие EntityManagerFactory
    }
}
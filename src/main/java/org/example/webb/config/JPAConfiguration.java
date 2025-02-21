package org.example.webb.config;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class JPAConfiguration {

    private static EntityManagerFactory emf;

    public static EntityManagerFactory getEntityManagerFactory() {
        if (emf == null) {
            try { // Укажите persistence unit
                emf = Persistence.createEntityManagerFactory("MyPersistenceUnit");
                System.out.println(emf.getProperties());
            } catch (Throwable ex) {
                System.err.println("Initial EntityManagerFactory creation failed." + ex);
                throw new ExceptionInInitializerError(ex);
            }
        }
        return emf;
    }

    public static void shutdown() {
        if (emf != null) {
            emf.close();
        }
    }
}
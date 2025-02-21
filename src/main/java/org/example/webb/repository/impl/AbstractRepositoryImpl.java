package org.example.webb.repository.impl;

import jakarta.persistence.EntityManager;
import org.example.webb.config.JPAConfiguration;

public abstract class AbstractRepositoryImpl<T> {

    private final Class<T> entityClass;

    protected AbstractRepositoryImpl(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    public EntityManager getEntityManager() {
        return JPAConfiguration.getEntityManagerFactory().createEntityManager(); // Создаем EntityManager здесь
    }

    public Class<T> getEntityClass() {
        return entityClass;
    }
}
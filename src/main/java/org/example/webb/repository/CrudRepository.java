package org.example.webb.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.PersistenceException;
import org.example.webb.exception.RepositoryException;

import java.util.List;

public interface CrudRepository<T, ID> {

    EntityManager getEntityManager(); // Метод для получения EntityManager

    Class<T> getEntityClass();       // Метод для получения класса сущности

    default T findById(ID id) {
        try {
            return getEntityManager().find(getEntityClass(), id);
        } catch (PersistenceException e) {
            throw new RepositoryException("Error finding entity by ID: " + id, e);
        }
    }

    default List<T> findAll() {
        try {
            return getEntityManager().createQuery("SELECT e FROM " + getEntityClass().getSimpleName() + " e",
                    getEntityClass()).getResultList();
        } catch (PersistenceException e) {
            throw new RepositoryException("Error finding all entities", e);
        }
    }

    default void save(T entity) {
        EntityManager entityManager = getEntityManager();
        entityManager.getTransaction().begin();
        try {
            entityManager.persist(entity);
            entityManager.getTransaction().commit();
        } catch (EntityExistsException e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw new RepositoryException("Entity already exists: " + entity, e);
        } catch (PersistenceException e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw new RepositoryException("Error saving entity: " + entity, e);
        }
    }

    default void delete(T entity) {
        EntityManager entityManager = getEntityManager();
        entityManager.getTransaction().begin();
        try {
            T mergedEntity = entityManager.merge(entity); // Присоединение к контексту, если нужно
            entityManager.remove(mergedEntity);
            entityManager.getTransaction().commit();
        } catch (PersistenceException e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw new RepositoryException("Error deleting entity: " + entity, e);
        }
    }

    default T merge(T entity) {
        EntityManager entityManager = getEntityManager();
        entityManager.getTransaction().begin();
        try {
            T mergedEntity = entityManager.merge(entity);
            entityManager.getTransaction().commit();
            return mergedEntity; // Возвращаем присоединенную сущность
        } catch (PersistenceException e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw new RepositoryException("Error merging entity: " + entity, e);
        }
    }
}
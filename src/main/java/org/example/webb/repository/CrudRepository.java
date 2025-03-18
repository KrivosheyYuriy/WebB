package org.example.webb.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.PersistenceException;
import org.example.webb.exception.RepositoryException;

import java.util.List;

public interface CrudRepository<T, ID> {

    EntityManager getEntityManager(); // Метод для получения EntityManager

    Class<T> getEntityClass();       // Метод для получения класса сущности

    default T findById(ID id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(getEntityClass(), id);
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    default List<T> findAll() {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery("SELECT e FROM " + getEntityClass().getSimpleName() + " e", getEntityClass()).getResultList();
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    default void save(T entity) {
        EntityManager em = getEntityManager();
        EntityTransaction transaction = null;
        try {
            transaction = em.getTransaction();
            transaction.begin();
            em.persist(entity);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw e; // Важно перебросить исключение
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
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

    default void deleteById(ID id) {
        EntityManager em = getEntityManager();
        EntityTransaction transaction = null;
        try {
            transaction = em.getTransaction();
            transaction.begin();
            T entity = em.find(getEntityClass(), id);
            if (entity != null) {
                em.remove(entity);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw e; // Важно перебросить исключение
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }
}
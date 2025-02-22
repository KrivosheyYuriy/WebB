package org.example.webb.repository.impl;

import jakarta.persistence.EntityManager;
import org.example.webb.entity.User;
import org.example.webb.repository.UserRepository;
import jakarta.persistence.TypedQuery;
import java.util.List;

public class UserRepositoryImpl extends AbstractRepositoryImpl<User> implements UserRepository {

    public UserRepositoryImpl() {
        super(User.class); // Вызов конструктора суперкласса, передаем только класс сущности
    }

    @Override
    public User findByUsername(String username) {
        TypedQuery<User> query = getEntityManager().createQuery
                ("SELECT u FROM User u WHERE u.username = :username", User.class);
        query.setParameter("username", username);
        List<User> results = query.getResultList();
        EntityManager em = null;
        try {
            em = getEntityManager();
            query = em.createQuery("SELECT u FROM User u WHERE u.username = :username", User.class);
            query.setParameter("username", username);
            results = query.getResultList();
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
        return results.isEmpty() ? null : results.get(0);
    }
}
package org.example.webb.repository.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.example.webb.entity.Admin;
import org.example.webb.repository.AdminRepository;

import java.util.List;

public class AdminRepositoryImpl extends AbstractRepositoryImpl<Admin> implements AdminRepository {
    public AdminRepositoryImpl() {
        super(Admin.class);
    }

    @Override
    public Admin findByUsername(String username) {
        TypedQuery<Admin> query = getEntityManager().createQuery
                ("SELECT a FROM Admin a WHERE a.username = :username", Admin.class);
        query.setParameter("username", username);
        List<Admin> results;
        EntityManager em = null;
        try {
            em = getEntityManager();
            query = em.createQuery("SELECT a FROM Admin a WHERE a.username = :username", Admin.class);
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

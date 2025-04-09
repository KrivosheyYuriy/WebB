package org.example.webb.repository.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.example.webb.entity.pollAnswerLanguage.PollAnswerLanguage;
import org.example.webb.repository.PollAnswerLanguageRepository;

import java.util.List;

public class PollAnswerLanguageRepositoryImpl extends AbstractRepositoryImpl<PollAnswerLanguage>
        implements PollAnswerLanguageRepository {
    public PollAnswerLanguageRepositoryImpl() {
        super(PollAnswerLanguage.class);
    }

    @Override
    public List<Object[]> countLanguages() {
        try {
            EntityManager em = getEntityManager();

            TypedQuery<Object[]> query = em.createQuery(
                    "SELECT MAX(p.language.title), COUNT(p) FROM PollAnswerLanguage p GROUP BY p.language.id",
                    Object[].class);

            return query.getResultList();
        } catch (Exception e) {
            return List.of();
        }
    }
}

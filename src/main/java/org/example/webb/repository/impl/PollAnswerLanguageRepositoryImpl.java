package org.example.webb.repository.impl;

import jakarta.persistence.TypedQuery;
import org.example.webb.entity.Language;
import org.example.webb.entity.PollAnswerLanguage;
import org.example.webb.repository.PollAnswerLanguageRepository;

public class PollAnswerLanguageRepositoryImpl extends AbstractRepositoryImpl<PollAnswerLanguage>
        implements PollAnswerLanguageRepository {
    public PollAnswerLanguageRepositoryImpl() {
        super(PollAnswerLanguage.class);
    }

    @Override
    public long countByLanguage(Language language) {
        try {
            TypedQuery<Long> query = getEntityManager().
                    createQuery("SELECT count(*) from PollAnswerLanguage p where p.id.languageId = :id",
                            Long.class);
            query.setParameter("id", language.getId());
            return query.getSingleResult();
        }
        catch (NullPointerException e) {
            return 0;
        }
    }
}

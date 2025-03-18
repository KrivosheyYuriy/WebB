package org.example.webb.repository;

import org.example.webb.entity.Language;
import org.example.webb.entity.PollAnswerLanguage;
import org.example.webb.entity.PollAnswerLanguageKey;

public interface PollAnswerLanguageRepository extends CrudRepository<PollAnswerLanguage, PollAnswerLanguageKey> {
    long countByLanguage(Language language);
}

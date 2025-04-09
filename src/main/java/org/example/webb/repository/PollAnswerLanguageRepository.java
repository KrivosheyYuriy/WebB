package org.example.webb.repository;

import org.example.webb.entity.pollAnswerLanguage.PollAnswerLanguage;
import org.example.webb.entity.pollAnswerLanguage.PollAnswerLanguageKey;

import java.util.List;

public interface PollAnswerLanguageRepository extends CrudRepository<PollAnswerLanguage, PollAnswerLanguageKey> {
    List<Object[]> countLanguages();
}

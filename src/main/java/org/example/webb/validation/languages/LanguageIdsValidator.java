package org.example.webb.validation.languages;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.example.webb.repository.impl.LanguageRepositoryImpl;


import java.util.List;

public class LanguageIdsValidator implements ConstraintValidator<ValidLanguageIds, List<Long>> {

    private final LanguageRepositoryImpl languageRepository = new LanguageRepositoryImpl();

    @Override
    public boolean isValid(List<Long> languageIds, ConstraintValidatorContext context) {
        if (languageIds == null || languageIds.isEmpty()) {
            return false;
        }

        for (Long languageId : languageIds) {
            if (languageId == null || languageRepository.findById(languageId) == null)
                return false;
        }

        return true;
    }
}
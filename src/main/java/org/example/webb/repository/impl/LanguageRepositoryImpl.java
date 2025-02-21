package org.example.webb.repository.impl;

import org.example.webb.entity.Language;

import org.example.webb.repository.LanguageRepository;

public class LanguageRepositoryImpl extends AbstractRepositoryImpl<Language> implements LanguageRepository {
    public LanguageRepositoryImpl() {
        super(Language.class); // Вызов конструктора суперкласса, передаем только класс сущности
    }
}

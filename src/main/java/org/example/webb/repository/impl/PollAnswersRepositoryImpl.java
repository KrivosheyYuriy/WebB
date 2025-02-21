package org.example.webb.repository.impl;

import org.example.webb.entity.PollAnswers;
import org.example.webb.repository.PollAnswersRepository;

public class PollAnswersRepositoryImpl extends AbstractRepositoryImpl<PollAnswers> implements PollAnswersRepository {
    public PollAnswersRepositoryImpl() {
        super(PollAnswers.class); // Вызов конструктора суперкласса, передаем только класс сущности
    }
}

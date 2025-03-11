package org.example.webb.repository.impl;

import org.example.webb.entity.PollAnswer;
import org.example.webb.repository.PollAnswersRepository;

public class PollAnswersRepositoryImpl extends AbstractRepositoryImpl<PollAnswer> implements PollAnswersRepository {
    public PollAnswersRepositoryImpl() {
        super(PollAnswer.class); // Вызов конструктора суперкласса, передаем только класс сущности
    }
}

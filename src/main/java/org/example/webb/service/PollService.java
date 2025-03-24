package org.example.webb.service;

import jakarta.transaction.Transactional;
import org.example.webb.dto.PollAnswerDTO;
import org.example.webb.entity.Language;
import org.example.webb.entity.PollAnswerLanguage;
import org.example.webb.entity.PollAnswer;
import org.example.webb.repository.LanguageRepository;
import org.example.webb.repository.PollAnswersRepository;


import java.time.LocalDate;
import java.time.LocalDateTime;


public class PollService {

    private final PollAnswersRepository pollRepository;
    private final LanguageRepository languageRepository;

    public PollService(PollAnswersRepository pollRepository, LanguageRepository languageRepository) {
        this.pollRepository = pollRepository;
        this.languageRepository = languageRepository;
    }

    @Transactional
    public PollAnswer addPoll(PollAnswerDTO formDto) {
        // Инициализация параметров
        String username = formDto.getName();
        String phoneNumber = formDto.getPhone();
        String email = formDto.getEmail();
        LocalDate birthday = formDto.getBirthday();
        String gender = formDto.getGender();
        String biography = formDto.getBiography();

        // Создание PollAnswers с использованием конструктора
        PollAnswer pollAnswer = new PollAnswer(
                username,
                birthday,
                gender,
                phoneNumber,
                email,
                biography,
                LocalDateTime.now(),
                null
        );

        // Сохранение PollAnswers, чтобы получить ID
        pollRepository.save(pollAnswer);

        // Преобразование языков из DTO в Entity и создание связей
        for (Long languageId : formDto.getLanguagesId()) {
            Language language = languageRepository.findById(languageId);
            if (language == null) {
                // Обработка ситуации, когда язык не найден
                System.err.println("Language not found with ID: " + languageId);
                continue; // Пропускаем язык, если он не найден
            }
            PollAnswerLanguage pollAnswerLanguage = new PollAnswerLanguage(pollAnswer, language);
            pollAnswer.addPollAnswerLanguage(pollAnswerLanguage);
        }

        // Merge PollAnswers (cascade persist должен сохранить PollAnswerLanguage)
        pollRepository.merge(pollAnswer); // Use merge to update
        return pollAnswer;
    }

    @Transactional
    public void updatePoll(PollAnswerDTO formDto, PollAnswer pollAnswer) {
        String username = formDto.getName();
        String phoneNumber = formDto.getPhone();
        String email = formDto.getEmail();
        LocalDate birthday = formDto.getBirthday();
        String gender = formDto.getGender();
        String biography = formDto.getBiography();

        pollAnswer.setUsername(username);
        pollAnswer.setPhoneNumber(phoneNumber);
        pollAnswer.setEmail(email);
        pollAnswer.setBirthday(birthday);
        pollAnswer.setGender(gender);
        pollAnswer.setBiography(biography);
        pollAnswer.getPollAnswersLanguages().clear();

        for (Long languageId : formDto.getLanguagesId()) {
            Language language = languageRepository.findById(languageId);
            if (language == null) {
                // Обработка ситуации, когда язык не найден
                System.err.println("Language not found with ID: " + languageId);
                continue; // Пропускаем язык, если он не найден
            }
            PollAnswerLanguage pollAnswerLanguage = new PollAnswerLanguage(pollAnswer, language);
            pollAnswer.addPollAnswerLanguage(pollAnswerLanguage);
        }

        pollAnswer.updateReceived();
        pollRepository.merge(pollAnswer);
    }
}
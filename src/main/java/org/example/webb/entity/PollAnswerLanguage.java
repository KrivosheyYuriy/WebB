package org.example.webb.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "poll_answer_languages") // Явное указание имени таблицы
public class PollAnswerLanguage {
    @EmbeddedId
    private PollAnswerLanguageKey id = new PollAnswerLanguageKey(); // Инициализация!

    @ManyToOne
    @MapsId("pollAnswerId") // Ссылка на поле pollAnswerId в PollAnswerLanguageKey
    @JoinColumn(name = "poll_answer_id")
    private PollAnswer answer;

    @ManyToOne
    @MapsId("languageId") // Ссылка на поле languageId в PollAnswerLanguageKey
    @JoinColumn(name = "language_id")
    private Language language;

    public PollAnswerLanguage(PollAnswer answer, Language language) {
        this.id = new PollAnswerLanguageKey(answer.getId(),  language.getId());
        this.answer = answer;
        this.language = language;
    }

    public PollAnswerLanguage() {}

    public PollAnswer getAnswer() {
        return answer;
    }

    public Language getLanguage() {
        return language;
    }
}
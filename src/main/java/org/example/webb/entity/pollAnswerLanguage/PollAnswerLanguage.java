package org.example.webb.entity.pollAnswerLanguage;

import jakarta.persistence.*;
import org.example.webb.entity.Language;
import org.example.webb.entity.PollAnswer;

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

    public PollAnswerLanguageKey getId() {
        return id;
    }

    public PollAnswerLanguage() {}

    public PollAnswer getAnswer() {
        return answer;
    }

    public Language getLanguage() {
        return language;
    }

    public void setAnswer(PollAnswer answer) {
        this.answer = answer;
    }

    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (!(o instanceof PollAnswerLanguage that))
            return false;

        return this.id.equals(that.id);
    }

    public int hashCode() {
        return this.id.hashCode();
    }
}
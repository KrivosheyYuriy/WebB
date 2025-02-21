package org.example.webb.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;  // Добавлен импорт для Objects

@Embeddable
public class PollAnswerLanguageKey implements Serializable {

    @Column(name = "poll_answer_id")
    private Long pollAnswerId;

    @Column(name = "language_id")
    private Long languageId;

    public PollAnswerLanguageKey() {
    }

    public PollAnswerLanguageKey(Long pollAnswerId, Long languageId) {
        this.pollAnswerId = pollAnswerId;
        this.languageId = languageId;
    }

    public Long getPollAnswerId() {
        return pollAnswerId;
    }

    public Long getLanguageId() {
        return languageId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PollAnswerLanguageKey that = (PollAnswerLanguageKey) o;
        return Objects.equals(pollAnswerId, that.pollAnswerId) && Objects.equals(languageId, that.languageId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pollAnswerId, languageId);
    }
}
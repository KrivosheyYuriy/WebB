package org.example.webb.entity;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Language {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @OneToMany(mappedBy = "language", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PollAnswerLanguage> pollAnswersLanguages = new HashSet<>();

    public Language(String title) {
        this.title = title;
    }

    public Language() {}

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Set<PollAnswerLanguage> getPollAnswersLanguages() {
        return pollAnswersLanguages;
    }

    public void setPollAnswersLanguages(Set<PollAnswerLanguage> pollAnswersLanguages) {
        this.pollAnswersLanguages = pollAnswersLanguages;
    }
}
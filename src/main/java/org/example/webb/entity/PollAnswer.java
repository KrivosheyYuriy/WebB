package org.example.webb.entity;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
public class PollAnswer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    private LocalDate birthday;

    private String gender;

    private String phoneNumber;

    private String email;

    private String biography;

    private LocalDateTime received;

    @OneToMany(mappedBy = "answer", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PollAnswerLanguage> pollAnswersLanguages = new HashSet<>();

    @OneToOne(cascade = CascadeType.ALL) // Добавляем связь
    @JoinColumn(name = "user_id") // Foreign key column in PollAnswer
    private User user; // Reference to User entity

    public PollAnswer(String username, LocalDate birthday, String gender, String phoneNumber, String email,
                      String biography, LocalDateTime received, User user) {
        this.username = username;
        this.birthday = birthday;
        this.gender = gender;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.biography = biography;
        this.received = received;
        this.pollAnswersLanguages = new HashSet<>();
        this.user = user; // Важно: устанавливаем связь в конструкторе
    }

    public PollAnswer() {
        this.pollAnswersLanguages = new HashSet<>();
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public String getGender() {
        return gender;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public Set<PollAnswerLanguage> getPollAnswersLanguages() {
        return pollAnswersLanguages;
    }

    public String getEmail() {
        return email;
    }

    public String getBiography() {
        return biography;
    }

    public LocalDateTime getReceived() {
        return received;
    }

    public void addPollAnswerLanguage(PollAnswerLanguage pollAnswerLanguage) {
        pollAnswersLanguages.add(pollAnswerLanguage);
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }
}
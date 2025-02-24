package org.example.webb.dto;

import jakarta.validation.constraints.*;
import org.example.webb.validation.date.Date;
import org.example.webb.validation.languages.ValidLanguageIds;
import org.example.webb.validation.phone.Phone;

import java.time.LocalDate;
import java.util.List;

public class PollAnswerDTO {
    @NotBlank(message = "ФИО не может быть пустым")
    @Size(min = 2, max = 100, message = "ФИО должно содержать от 2 до 100 символов")
    @Pattern(regexp = "^([А-ЯЁ][а-яё]+[\\-\\s]?){3,}$", message = "ФИО может содержать только буквы, пробелы и дефисы")
    private final String name;

    @NotBlank(message = "Телефон не может быть пустым")
    @Phone
    private final String phone;

    @NotBlank(message = "Email не может быть пустым")
    @Email(message = "Некорректный формат email")
    private final String email;

    @NotNull(message = "Дата рождения не может быть пустой")
    @Date
    private final LocalDate birthday;

    @NotBlank(message = "Пол не может быть пустым")
    @Pattern(regexp = "(?i)^(мужской|женский)$", message = "Пол должен быть 'мужской' или 'женский'")
    private final String gender;

    @Size(min = 1, message = "Выберите хотя бы один язык программирования")
    @ValidLanguageIds
    private final List<Long> languagesId;

    private final String biography;

    public PollAnswerDTO(String name, String phone, String email, LocalDate birthday, String gender,
                         List<Long> languagesId, String biography) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.birthday = birthday;
        this.gender = gender;
        this.languagesId = languagesId;
        this.biography = biography;
    }

    public String getBiography() {
        return biography;
    }

    public List<Long> getLanguagesId() {
        return languagesId;
    }

    public String getGender() {
        return gender;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getName() {
        return name;
    }
}
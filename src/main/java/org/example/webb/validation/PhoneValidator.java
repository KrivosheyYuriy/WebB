package org.example.webb.validation;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PhoneValidator implements ConstraintValidator<Phone, String> {

    @Override
    public boolean isValid(String phone, ConstraintValidatorContext context) {
        if (phone == null) {
            return false;
        }
        // Простая проверка формата: только цифры и длина 11 символов
        return phone.matches("^(\\+7|8)([0-9]{10})$");
    }
}
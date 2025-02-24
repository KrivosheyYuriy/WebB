package org.example.webb.validation.date;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

public class DateValidator implements ConstraintValidator<Date, LocalDate> {
    @Override
    public boolean isValid(LocalDate date, ConstraintValidatorContext context) {
        if (date == null) {
            return false;
        }

        return date.isBefore(LocalDate.now().minusDays(1)) && 
                date.isAfter(LocalDate.of(1920, 1, 1).minusDays(1));
    }
}

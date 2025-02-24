package org.example.webb.validation.date;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = DateValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Date {
    String message() default "Дата выходит за рамки допустимых границ";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

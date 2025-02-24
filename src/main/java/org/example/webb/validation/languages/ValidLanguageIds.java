package org.example.webb.validation.languages;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = LanguageIdsValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidLanguageIds {

    String message() default "Некорректные id языков";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

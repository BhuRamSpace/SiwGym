package it.uniroma3.siw.controller.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = MaxParticipantsValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface MaxParticipantsConstraint {
    String message() default "Il numero massimo di partecipanti non può superare la capacità massima del corso.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
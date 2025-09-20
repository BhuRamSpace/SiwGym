package it.uniroma3.siw.controller.validator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = TimeOrderValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface TimeOrderConstraint {
    String message() default "L'orario di fine deve essere successivo all'orario di inizio.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
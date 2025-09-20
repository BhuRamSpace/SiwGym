package it.uniroma3.siw.controller.validator;

import it.uniroma3.siw.model.CourseSlot;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class MaxParticipantsValidator implements ConstraintValidator<MaxParticipantsConstraint, CourseSlot> {

    @Override
    public boolean isValid(CourseSlot courseSlot, ConstraintValidatorContext context) {
        if (courseSlot.getMaxParticipants() == null || courseSlot.getCourse() == null) {
            return false;
        }

        if (courseSlot.getMaxParticipants() > courseSlot.getCourse().getMaxCapacity()) {
            // Disabilita il messaggio di errore di default
            context.disableDefaultConstraintViolation();
            
            // Aggiunge un messaggio di errore specifico per il campo 'maxParticipants'
            context.buildConstraintViolationWithTemplate(
                "Il numero massimo di partecipanti non può superare la capacità massima del corso.")
                .addPropertyNode("maxParticipants") // Collega l'errore al campo maxParticipants
                .addConstraintViolation();
            
            return false;
        }
        
        return true;
    }
}


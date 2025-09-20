package it.uniroma3.siw.controller.validator;

import it.uniroma3.siw.model.CourseSlot;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class TimeOrderValidator implements ConstraintValidator<TimeOrderConstraint, CourseSlot> {

    @Override
    public boolean isValid(CourseSlot courseSlot, ConstraintValidatorContext context) {
        if (courseSlot.getStartTime() == null || courseSlot.getEndTime() == null) {
            return false;
        }
        
        if (!courseSlot.getEndTime().isAfter(courseSlot.getStartTime())) {
            // Disabilita il messaggio di errore di default
            context.disableDefaultConstraintViolation();
            
            // Collega l'errore al campo 'endTime'
            context.buildConstraintViolationWithTemplate(
                "Orario di fine deve essere successivo all'orario di inizio.")
                .addPropertyNode("endTime") // Collega l'errore al campo endTime
                .addConstraintViolation();
            
            return false;
        }
        
        return true;
    }
}
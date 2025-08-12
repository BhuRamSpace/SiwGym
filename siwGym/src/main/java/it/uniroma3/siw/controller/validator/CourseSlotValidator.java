package it.uniroma3.siw.controller.validator;

import it.uniroma3.siw.model.CourseSlot;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class CourseSlotValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return CourseSlot.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        CourseSlot courseSlot = (CourseSlot) target;

        if (courseSlot.getStartTime() != null && courseSlot.getEndTime() != null) {
            // Controlla che l'orario di inizio sia prima dell'orario di fine
            if (!courseSlot.getStartTime().isBefore(courseSlot.getEndTime())) {
                errors.rejectValue("endTime", "invalid.courseSlot.endTime", "L'orario di fine deve essere successivo a quello di inizio.");
            }
        }
    }
}
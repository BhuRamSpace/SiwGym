package it.uniroma3.siw.controller.validator;

import it.uniroma3.siw.model.Subscription;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Date;

@Component
public class SubscriptionValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return Subscription.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Subscription subscription = (Subscription) target;

        if (subscription.getStartDate() != null && subscription.getEndDate() != null) {
            // Controlla che la data di fine sia successiva a quella di inizio
            if (!subscription.getEndDate().after(subscription.getStartDate())) {
                errors.rejectValue("endDate", "invalid.subscription.endDate", "La data di fine deve essere successiva a quella di inizio.");
            }
            // Opzionalmente, potresti aggiungere un controllo per abbonamenti sovrapposti
        }
    }
}
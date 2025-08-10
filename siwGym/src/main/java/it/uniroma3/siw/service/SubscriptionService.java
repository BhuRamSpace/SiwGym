package it.uniroma3.siw.service;

import it.uniroma3.siw.model.Subscription;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.repository.SubscriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Date;
import java.util.Optional;

@Service
public class SubscriptionService {

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Transactional
    public Subscription save(Subscription subscription) {
        return subscriptionRepository.save(subscription);
    }

    public Optional<Subscription> findById(Long id) {
        return subscriptionRepository.findById(id);
    }

    public Iterable<Subscription> findAll() {
        return subscriptionRepository.findAll();
    }
    
    // Metodo per trovare l'abbonamento di un utente specifico
    public Optional<Subscription> findByUser(User user) {
        return subscriptionRepository.findByUser(user);
    }

    @Transactional
    public void deleteById(Long id) {
        subscriptionRepository.deleteById(id);
    }
    
    public boolean isValidSubscription(User user) {
        Optional<Subscription> subscriptionOptional = subscriptionRepository.findByUser(user);
        if (subscriptionOptional.isPresent()) {
            Subscription subscription = subscriptionOptional.get();
            // Controlla se la data di fine abbonamento è successiva alla data odierna
            return subscription.getEndDate().after(new Date());
        }
        return false;
    }
}
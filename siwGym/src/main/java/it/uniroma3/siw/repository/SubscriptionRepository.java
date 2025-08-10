package it.uniroma3.siw.repository;

import it.uniroma3.siw.model.Subscription;
import it.uniroma3.siw.model.User;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubscriptionRepository extends CrudRepository<Subscription, Long> {
	
	public Optional<Subscription> findByUser(User user);
	
}
package it.uniroma3.siw.repository;

import it.uniroma3.siw.model.User;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
	
	long count();

	Optional<User> findByCredentialsUsername(String username);
}
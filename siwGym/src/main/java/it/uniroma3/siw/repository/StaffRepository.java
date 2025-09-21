package it.uniroma3.siw.repository;

import it.uniroma3.siw.model.Staff;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StaffRepository extends CrudRepository<Staff, Long> {
	
	Optional<Staff> findByCredentialsUsername(String username);
	long count();
	
}
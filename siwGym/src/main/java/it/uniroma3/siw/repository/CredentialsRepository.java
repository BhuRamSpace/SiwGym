package it.uniroma3.siw.repository;

import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.Staff;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CredentialsRepository extends CrudRepository<Credentials, Long> {
	
	public Optional<Credentials> findByUsername(String username);
    	   Optional<Credentials> findByStaff(Staff staff);
    	   List<Credentials> findByRole(String role);
    	   //long countByRole(String role);
}
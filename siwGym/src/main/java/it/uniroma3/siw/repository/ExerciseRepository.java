package it.uniroma3.siw.repository;

import it.uniroma3.siw.model.Exercise;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExerciseRepository extends CrudRepository<Exercise, Long> {
	
	public Optional<Exercise> findByName(String name);
	
}
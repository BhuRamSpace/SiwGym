package it.uniroma3.siw.repository;

import it.uniroma3.siw.model.Course;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseRepository extends CrudRepository<Course, Long> {
	
	public Optional<Course> findByName(String name);
	long count();
	
}
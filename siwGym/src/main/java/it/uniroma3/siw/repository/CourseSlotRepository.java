package it.uniroma3.siw.repository;

import it.uniroma3.siw.model.Course;
import it.uniroma3.siw.model.CourseSlot;
import it.uniroma3.siw.model.Staff;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseSlotRepository extends CrudRepository<CourseSlot, Long> {
	
	public List<CourseSlot> findByCourse(Course course);
	public List<CourseSlot> findByTrainer(Staff trainer);
	void deleteByTrainer(Staff trainer);
	long count();
	
}
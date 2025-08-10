package it.uniroma3.siw.repository;

import it.uniroma3.siw.model.Staff;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.model.WorkoutPlan;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkoutPlanRepository extends CrudRepository<WorkoutPlan, Long> {
	public List<WorkoutPlan> findByUser(User user);
	public List<WorkoutPlan> findByTrainer(Staff trainer);
}
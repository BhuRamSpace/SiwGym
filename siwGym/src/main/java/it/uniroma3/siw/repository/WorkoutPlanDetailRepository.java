package it.uniroma3.siw.repository;

import it.uniroma3.siw.model.WorkoutPlan;
import it.uniroma3.siw.model.WorkoutPlanDetail;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkoutPlanDetailRepository extends CrudRepository<WorkoutPlanDetail, Long> {
	public List<WorkoutPlanDetail> findByWorkoutPlan(WorkoutPlan workoutPlan);
}
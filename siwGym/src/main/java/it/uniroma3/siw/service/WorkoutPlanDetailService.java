package it.uniroma3.siw.service;

import it.uniroma3.siw.model.WorkoutPlan;
import it.uniroma3.siw.model.WorkoutPlanDetail;
import it.uniroma3.siw.repository.WorkoutPlanDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.List;

@Service
public class WorkoutPlanDetailService {

    @Autowired
    private WorkoutPlanDetailRepository workoutPlanDetailRepository;

    @Transactional
    public WorkoutPlanDetail save(WorkoutPlanDetail workoutPlanDetail) {
        return workoutPlanDetailRepository.save(workoutPlanDetail);
    }

    public Optional<WorkoutPlanDetail> findById(Long id) {
        return workoutPlanDetailRepository.findById(id);
    }

    public Iterable<WorkoutPlanDetail> findAll() {
        return workoutPlanDetailRepository.findAll();
    }
    
    // Metodo per trovare tutti i dettagli di una scheda di allenamento
    public List<WorkoutPlanDetail> findByWorkoutPlan(WorkoutPlan workoutPlan) {
        return workoutPlanDetailRepository.findByWorkoutPlan(workoutPlan);
    }

    @Transactional
    public void deleteById(Long id) {
        workoutPlanDetailRepository.deleteById(id);
    }
}
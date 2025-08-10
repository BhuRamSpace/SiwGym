package it.uniroma3.siw.service;

import it.uniroma3.siw.model.WorkoutPlan;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.model.Staff;
import it.uniroma3.siw.repository.WorkoutPlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.List;

@Service
public class WorkoutPlanService {

    @Autowired
    private WorkoutPlanRepository workoutPlanRepository;

    @Transactional
    public WorkoutPlan save(WorkoutPlan workoutPlan) {
        return workoutPlanRepository.save(workoutPlan);
    }

    public Optional<WorkoutPlan> findById(Long id) {
        return workoutPlanRepository.findById(id);
    }

    public Iterable<WorkoutPlan> findAll() {
        return workoutPlanRepository.findAll();
    }
    
    // Metodo per trovare le schede di allenamento di un utente
    public List<WorkoutPlan> findByUser(User user) {
        return workoutPlanRepository.findByUser(user);
    }

    // Metodo per trovare le schede di allenamento create da un trainer
    public List<WorkoutPlan> findByTrainer(Staff trainer) {
        return workoutPlanRepository.findByTrainer(trainer);
    }

    @Transactional
    public void deleteById(Long id) {
        workoutPlanRepository.deleteById(id);
    }
}
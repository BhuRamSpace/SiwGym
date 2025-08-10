package it.uniroma3.siw.service;

import it.uniroma3.siw.model.Exercise;
import it.uniroma3.siw.repository.ExerciseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class ExerciseService {

    @Autowired
    private ExerciseRepository exerciseRepository;

    @Transactional
    public Exercise save(Exercise exercise) {
        return exerciseRepository.save(exercise);
    }

    public Optional<Exercise> findById(Long id) {
        return exerciseRepository.findById(id);
    }

    public Iterable<Exercise> findAll() {
        return exerciseRepository.findAll();
    }
    
    // Metodo per trovare un esercizio tramite il suo nome
    public Optional<Exercise> findByName(String name) {
        return exerciseRepository.findByName(name);
    }

    @Transactional
    public void deleteById(Long id) {
        exerciseRepository.deleteById(id);
    }
}
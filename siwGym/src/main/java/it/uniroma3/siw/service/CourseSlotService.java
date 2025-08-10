package it.uniroma3.siw.service;

import it.uniroma3.siw.model.Course;
import it.uniroma3.siw.model.CourseSlot;
import it.uniroma3.siw.model.Staff;
import it.uniroma3.siw.repository.CourseSlotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.List;

@Service
public class CourseSlotService {

    @Autowired
    private CourseSlotRepository courseSlotRepository;

    @Transactional
    public CourseSlot save(CourseSlot courseSlot) {
        return courseSlotRepository.save(courseSlot);
    }

    public Optional<CourseSlot> findById(Long id) {
        return courseSlotRepository.findById(id);
    }

    public Iterable<CourseSlot> findAll() {
        return courseSlotRepository.findAll();
    }
    
    // Metodo per trovare tutte le lezioni di un corso
    public List<CourseSlot> findByCourse(Course course) {
        return courseSlotRepository.findByCourse(course);
    }

    // Metodo per trovare tutte le lezioni tenute da un trainer
    public List<CourseSlot> findByTrainer(Staff trainer) {
        return courseSlotRepository.findByTrainer(trainer);
    }

    @Transactional
    public void deleteById(Long id) {
        courseSlotRepository.deleteById(id);
    }
}
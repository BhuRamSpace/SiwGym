package it.uniroma3.siw.service;

import it.uniroma3.siw.model.Course;
import it.uniroma3.siw.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class CourseService {

    @Autowired
    private CourseRepository courseRepository;

    @Transactional
    public Course save(Course course) {
        return courseRepository.save(course);
    }

    public Optional<Course> findById(Long id) {
        return courseRepository.findById(id);
    }

    public Iterable<Course> findAll() {
        return courseRepository.findAll();
    }
    
    // Metodo per trovare un corso tramite il suo nome
    public Optional<Course> findByName(String name) {
        return courseRepository.findByName(name);
    }

    @Transactional
    public void deleteById(Long id) {
        courseRepository.deleteById(id);
    }

    public long count() {
        return courseRepository.count();
	}
}
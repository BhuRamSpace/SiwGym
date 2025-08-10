package it.uniroma3.siw.service;

import it.uniroma3.siw.model.Staff;
import it.uniroma3.siw.repository.StaffRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class StaffService {

    @Autowired
    private StaffRepository staffRepository;

    @Transactional
    public Staff save(Staff staff) {
        return staffRepository.save(staff);
    }

    public Optional<Staff> findById(Long id) {
        return staffRepository.findById(id);
    }
    
    public Iterable<Staff> findAll() {
        return staffRepository.findAll();
    }
    
    @Transactional
    public void deleteById(Long id) {
        staffRepository.deleteById(id);
    }
}
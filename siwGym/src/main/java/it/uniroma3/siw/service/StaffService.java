package it.uniroma3.siw.service;

import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.Staff;
import it.uniroma3.siw.repository.CredentialsRepository;
import it.uniroma3.siw.repository.StaffRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class StaffService {

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private CredentialsRepository credentialsRepository;
    
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
    
    /**
     * Finds all staff members with the 'TRAINER' role by querying the Credentials table.
     * @return A list of Staff entities who are trainers.
     */
    public List<Staff> findTrainers() {
        List<Credentials> trainerCredentials = credentialsRepository.findByRole("TRAINER");
        List<Staff> trainers = new ArrayList<>();
        for (Credentials c : trainerCredentials) {
            if (c.getStaff() != null) {
                trainers.add(c.getStaff());
            }
        }
        return trainers;
    }

	public long count() {
		return staffRepository.count();
	}
	
    public Optional<Staff> findByUsername(String username) {
        return staffRepository.findByCredentialsUsername(username);
    }

}
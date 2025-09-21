package it.uniroma3.siw.service;

import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.Staff;
import it.uniroma3.siw.repository.CredentialsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class CredentialsService {

    @Autowired
    private CredentialsRepository credentialsRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public Credentials save(Credentials credentials) {
        // Cripta la password prima di salvarla
        credentials.setPassword(passwordEncoder.encode(credentials.getPassword()));
        return credentialsRepository.save(credentials);
    }

    public Optional<Credentials> findByUsername(String username) {
        return credentialsRepository.findByUsername(username);
    }

    public Iterable<Credentials> findAll() {
        return credentialsRepository.findAll();
    }

    @Transactional
    public void deleteById(Long id) {
        credentialsRepository.deleteById(id);
    }
    
    public Optional<Credentials> findByStaff(Staff staff) {
        return credentialsRepository.findByStaff(staff);
    }

    public void delete(Credentials credentials) {
        credentialsRepository.delete(credentials);
    }

    public long countByRole(String role) {
        return credentialsRepository.countByRole(role);
    }

}
package it.uniroma3.siw.service;

import it.uniroma3.siw.model.User;
import it.uniroma3.siw.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public User save(User user) {
        return userRepository.save(user);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public Iterable<User> findAll() {
        return userRepository.findAll();
    }

    @Transactional
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }
    
    @Transactional
    public void delete(User user) {
        userRepository.delete(user);
    }

	public long count() {
		 return userRepository.count();
	}
	
	public User findByUsername(String username) {
        return userRepository.findByCredentialsUsername(username).orElse(null);
    }
	
    public List<User> findByNameOrSurname(String query) {
        return userRepository.findByNameContainingIgnoreCaseOrSurnameContainingIgnoreCase(query, query);
    }
}
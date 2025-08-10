package it.uniroma3.siw.service;

import it.uniroma3.siw.model.Booking;
import it.uniroma3.siw.model.CourseSlot;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.List;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Transactional
    public Booking save(Booking booking) {
        return bookingRepository.save(booking);
    }

    public Optional<Booking> findById(Long id) {
        return bookingRepository.findById(id);
    }

    public Iterable<Booking> findAll() {
        return bookingRepository.findAll();
    }

    // Metodo per trovare tutte le prenotazioni per una singola lezione
    public List<Booking> findByCourseSlot(CourseSlot courseSlot) {
        return bookingRepository.findByCourseSlot(courseSlot);
    }
    
    // Metodo per trovare tutte le prenotazioni di un utente specifico
    public List<Booking> findByUser(User user) {
        return bookingRepository.findByUser(user);
    }
    
    // Per contare le prenotazioni per una singola lezione
    public Long countBookingsForSlot(CourseSlot courseSlot) {
        return bookingRepository.countByCourseSlot(courseSlot);
    }
    
    // Per verificare se un utente ha già prenotato una specifica lezione
    public boolean isBookedByUser(CourseSlot courseSlot, User user) {
        return bookingRepository.findByCourseSlotAndUser(courseSlot, user).isPresent();
    }

    @Transactional
    public void deleteById(Long id) {
        bookingRepository.deleteById(id);
    }
}
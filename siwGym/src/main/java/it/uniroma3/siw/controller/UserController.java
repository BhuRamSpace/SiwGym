package it.uniroma3.siw.controller;

import it.uniroma3.siw.model.*;
import it.uniroma3.siw.service.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/user")
@PreAuthorize("hasAnyAuthority('DEFAULT', 'ADMIN')")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private CourseSlotService courseSlotService;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private CredentialsService credentialsService;

    // --- Dashboard Utente ---
    
    @GetMapping({"userDashboard"})
    public String indexUser(Model model) {
        long bookingCount = bookingService.count();
        

        model.addAttribute("bookingCount", bookingCount);
        return "user/userDashboard";
    }

    // --- Gestione Profilo Utente ---
    @GetMapping("/profile")
    public String viewProfile(Model model) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Credentials credentials = credentialsService.findByUsername(userDetails.getUsername()).orElse(null);

        if (credentials != null) {
            model.addAttribute("user", credentials.getUser());
            model.addAttribute("credentials", credentials);
            return "user/userProfile";
        }
        return "errorPage";
    }

    @GetMapping("/profile/editProfile")
    public String showEditProfileForm(Model model) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Credentials credentials = credentialsService.findByUsername(userDetails.getUsername()).orElse(null);

        if (credentials != null) {
            model.addAttribute("user", credentials.getUser());
            model.addAttribute("credentials", credentials);
            return "user/editProfileForm";
        }
        return "errorPage";
    }

    @PostMapping("/profile/editProfile")
    public String editProfile(@Valid @ModelAttribute("user") User userForm,
                              BindingResult bindingResult,
                              @ModelAttribute("credentials") Credentials credentialsForm,
                              Model model) {

        if (!bindingResult.hasErrors()) {
            // recupero l'utente loggato
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Credentials credentials = credentialsService.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("Credenziali non trovate"));

            User dbUser = credentials.getUser();

            // aggiorno solo i campi ammessi per l'utente
            dbUser.setName(userForm.getName());
            dbUser.setSurname(userForm.getSurname());
            dbUser.setEmail(userForm.getEmail());
            // aggiungi qui altri campi modificabili del profilo

            userService.save(dbUser);

            // aggiorno solo i campi ammessi per le credenziali
            if (credentialsForm.getPassword() != null && !credentialsForm.getPassword().isBlank()) {
                credentials.setPassword(credentialsForm.getPassword());
            }
            // NON tocco credentials.setRole()

            credentialsService.save(credentials);

            return "redirect:/user/profile";
        }

        // se ci sono errori, ricarico la form
        model.addAttribute("user", userForm);
        model.addAttribute("credentials", credentialsForm);
        return "user/editProfileForm";
    }


    
    //// ---- Gestione corsi e slot corsi ----
    /**
     * Mostra la lista di tutti i corsi disponibili per l'utente.
     */
    @GetMapping("/activeCourse")
    public String viewCourses(Model model) {
        model.addAttribute("courses", courseService.findAll());
        return "user/userCourses";
    }

    /**
     * Mostra i dettagli di un corso specifico e i suoi slot.
     * Mostra anche se l'utente ha già prenotato uno slot.
     */
    @GetMapping("/viewCourseSlots/{courseId}")
    public String viewCourseSlots(@PathVariable("courseId") Long courseId, Model model) {
        Optional<Course> courseOptional = courseService.findById(courseId);
        
        if (courseOptional.isEmpty()) {
            return "redirect:/user/courses";
        }
        
        Course course = courseOptional.get();
        model.addAttribute("course", course);

        // Recupera l'utente corrente autenticato
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        User currentUser = userService.findByUsername(currentUsername);

        // Ottieni gli ID degli slot prenotati dall'utente corrente
        Set<Long> userBookedSlots = currentUser.getBookings().stream()
                                            .map(booking -> booking.getCourseSlot().getId())
                                            .collect(Collectors.toSet());
        model.addAttribute("userBookedSlots", userBookedSlots);

        return "user/viewCourseSlots";
    }

    
    /**
     * Gestisce la prenotazione di uno slot da parte dell'utente e reindirizza alla pagina di conferma.
     */
    @GetMapping("/bookSlot/{slotId}")
    public String bookSlot(@PathVariable("slotId") Long slotId, Model model) {
        // Recupera l'utente corrente
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        User currentUser = userService.findByUsername(currentUsername);

        Optional<CourseSlot> slotOptional = courseSlotService.findById(slotId);
        
        if (slotOptional.isPresent()) {
            CourseSlot courseSlot = slotOptional.get();
            
            // Controlla se ci sono posti disponibili
            if (courseSlot.getBookings().size() < courseSlot.getMaxParticipants()) {
                // Crea una nuova prenotazione e salvala
                Booking booking = new Booking();
                booking.setUser(currentUser);
                booking.setCourseSlot(courseSlot);
                
                // Add the booking to the slot's list and save it
                courseSlot.addBooking(booking);
                courseSlotService.save(courseSlot);
                
                // Aggiungi gli attributi necessari al modello per la pagina di conferma
                model.addAttribute("booking", booking);
                model.addAttribute("user", currentUser);
                model.addAttribute("slot", courseSlot);
                model.addAttribute("course", courseSlot.getCourse());
                model.addAttribute("trainer", courseSlot.getTrainer());
                
                // Reindirizza alla pagina di conferma
                return "user/confirmationBooking";
            }
        }
        
        // Se la prenotazione fallisce, reindirizza alla lista dei corsi
        return "redirect:/user/courses";
    }
    
    
    /**
     * Gestisce l'eliminazione di una prenotazione da parte dell'utente.
     */
    
    /**
     * Gestisce l'eliminazione di una prenotazione da parte dell'utente.
     */
    
    @GetMapping("/deleteBooking/{slotId}")
    public String deleteBooking(@PathVariable("slotId") Long slotId, @RequestParam(value = "source", required = false) String source) {
        // Recupera l'utente corrente
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        User currentUser = userService.findByUsername(currentUsername);

        // Trova la prenotazione da eliminare
        Optional<Booking> bookingOptional = bookingService.findByUserIdAndCourseSlotId(currentUser.getId(), slotId);
        
        if (bookingOptional.isPresent()) {
            Booking booking = bookingOptional.get();
            CourseSlot courseSlot = booking.getCourseSlot();
            
            // Elimina la prenotazione.
            courseSlot.getBookings().remove(booking);
            
            // Salva lo slot aggiornato.
            courseSlotService.save(courseSlot);
            
            // Controlla il valore del parametro 'source' per decidere il reindirizzamento
            if ("myBookings".equals(source)) {
                // Se la richiesta proviene da myBookings, reindirizza lì
                return "redirect:/user/myBookings";
            } else {
                // Altrimenti, reindirizza a viewCourseSlots (comportamento predefinito)
                return "redirect:/user/viewCourseSlots/" + courseSlot.getCourse().getId();
            }
        }

        // Se la prenotazione non viene trovata, reindirizza alla lista dei corsi
        return "redirect:/user/courses";
    }
    
    
    @GetMapping("/myBookings")
    public String myBookings(Model model) {
        // Recupera l'utente corrente
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        User currentUser = userService.findByUsername(currentUsername);

        // Recupera le prenotazioni dell'utente e le aggiunge al modello
        List<Booking> userBookings = currentUser.getBookings();
        model.addAttribute("bookings", userBookings);
        model.addAttribute("user", currentUser);

        return "user/myBookings";
    }
}
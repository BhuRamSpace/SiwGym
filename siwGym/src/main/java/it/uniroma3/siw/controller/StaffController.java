package it.uniroma3.siw.controller;

import it.uniroma3.siw.model.*;
import it.uniroma3.siw.service.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Controller
@RequestMapping("/staff")
@PreAuthorize("hasAnyAuthority('ADMIN', 'TRAINER')")
public class StaffController {

    @Autowired
    private StaffService staffService;
    
    @Autowired
    private BookingService bookingService;

    @Autowired
    private CourseSlotService courseSlotService;

    @Autowired
    private UserService userService;
    
    @Autowired
    private CredentialsService credentialsService;
    
    @Autowired 
    private CourseService courseService;

    // --- Dashboard Staff ---
    
   /* @GetMapping({"", "/index"})
    public String indexStaff() {
        return "staff/indexStaff";
    }*/
    
    
 // --- Dashboard Trainer ---
    @GetMapping("/trainerDashboard")
    @PreAuthorize("hasAuthority('TRAINER')")
    public String trainerDashboard(Model model) {

        // Recupera il trainer loggato
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = null;
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        }

        // Trova lo Staff corrispondente allo username
        Staff trainer = staffService.findByUsername(username).orElse(null);
        if (trainer == null) {
            // se non trovato, reindirizza al logout o pagina di errore
            return "redirect:/logout";
        }

        // Recupera gli slot corsi assegnati a questo trainer
        List<CourseSlot> courseSlots = courseSlotService.findByTrainer(trainer);

        // Passa i dati alla view
        model.addAttribute("trainer", trainer);
        model.addAttribute("courseSlots", courseSlots);

        return "staff/trainerDashboard"; // path del template HTML
    }

    
    @GetMapping("/mySlots")
    @PreAuthorize("hasAuthority('TRAINER')")
    public String mySlots(Model model) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Staff trainer = staffService.findByUsername(userDetails.getUsername()).orElse(null);
        
        if (trainer != null) {
            // Aggiungi il trainer e i suoi slot al modello
            model.addAttribute("trainer", trainer);
            model.addAttribute("courseSlots", trainer.getCourseSlots());
            return "staff/manageStaffFolder/mySlots";
        }
        
        // Gestione dell'errore
        return "redirect:/logout";
    }
    
    
    @GetMapping("/viewBookings/{slotId}")
    @PreAuthorize("hasAuthority('TRAINER')")
    public String viewBookings(@PathVariable("slotId") Long slotId, 
                               @RequestParam(value = "name", required = false) String name, 
                               Model model) {
        Optional<CourseSlot> slotOptional = courseSlotService.findById(slotId);
        
        if (slotOptional.isPresent()) {
            CourseSlot courseSlot = slotOptional.get();
            List<Booking> bookings = courseSlot.getBookings();
            
            boolean isSearchAttempted = (name != null && !name.isBlank());
            
            // Se è presente un parametro di ricerca, filtra la lista
            if (isSearchAttempted) {
                bookings = bookings.stream()
                                   .filter(booking -> 
                                       booking.getUser().getName().toLowerCase().contains(name.toLowerCase()) ||
                                       booking.getUser().getSurname().toLowerCase().contains(name.toLowerCase())
                                   )
                                   .collect(Collectors.toList());
            }
            
            model.addAttribute("courseSlot", courseSlot);
            model.addAttribute("bookings", bookings);
            model.addAttribute("name", name); // Aggiungi questo attributo per mantenere il valore nel form
            model.addAttribute("isSearchAttempted", isSearchAttempted); // Aggiungi questo per la logica di visualizzazione
            return "staff/manageStaffFolder/viewBookings";
        }
        
        // Se lo slot non viene trovato
        return "redirect:/staff/mySlots";
    }

    /**
     * Gestisce l'eliminazione di una prenotazione da parte del trainer.
     */
    @GetMapping("/deleteBookingFromTrainer/{bookingId}")
    @PreAuthorize("hasAuthority('TRAINER')")
    public String deleteBookingFromTrainer(@PathVariable("bookingId") Long bookingId) {
        Optional<Booking> bookingOptional = bookingService.findById(bookingId);
        
        if (bookingOptional.isPresent()) {
            Booking booking = bookingOptional.get();
            Long courseSlotId = booking.getCourseSlot().getId();
            
            // Elimina la prenotazione
            bookingService.delete(booking);
            
            // Reindirizza alla pagina della lista dei prenotati
            return "redirect:/staff/viewBookings/" + courseSlotId;
        }
        
        return "redirect:/staff/mySlots";
    }
    
    /**
     * Visualizza il form per il trainer per prenotare un utente.
     */
    @GetMapping("/bookUser")
    @PreAuthorize("hasAuthority('TRAINER')")
    public String showBookUserForm(Model model) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Staff trainer = staffService.findByUsername(userDetails.getUsername()).orElse(null);

        if (trainer != null) {
            List<CourseSlot> trainerSlots = courseSlotService.findByTrainer(trainer);

            // Correct the conversion here
            Iterable<User> usersIterable = userService.findAll();
            List<User> users = StreamSupport.stream(usersIterable.spliterator(), false)
                                            .collect(Collectors.toList());

            model.addAttribute("courseSlots", trainerSlots);
            model.addAttribute("users", users);
            return "staff/manageStaffFolder/bookUserForm";
        }
        
        return "redirect:/logout";
    }
    
    /**
     * Gestisce la richiesta di prenotazione.
     */
    @PostMapping("/bookUser")
    @PreAuthorize("hasAuthority('TRAINER')")
    public String bookUser(@RequestParam("slotId") Long slotId,
                           @RequestParam("userId") Long userId,
                           Model model) {
        
        CourseSlot courseSlot = courseSlotService.findById(slotId).orElse(null);
        User user = userService.findById(userId).orElse(null);
        
        if (courseSlot != null && user != null) {
            if (courseSlot.getBookings().size() < courseSlot.getMaxParticipants()) {
                // Controlla se l'utente è già prenotato
                boolean alreadyBooked = courseSlot.getBookings().stream()
                                                .anyMatch(b -> b.getUser().getId().equals(user.getId()));
                
                if (alreadyBooked) {
                    return "redirect:/staff/bookUser?error=alreadyBooked";
                }
                
                Booking booking = new Booking();
                booking.setCourseSlot(courseSlot);
                booking.setUser(user);
                
                courseSlot.addBooking(booking);
                courseSlotService.save(courseSlot);
                
                return "redirect:/staff/bookUser?success=true";
            }
        }
        
        return "redirect:/staff/bookUser?error=true";
    }    

    // --- Gestione Lezioni (CourseSlot) ---
/*
    @GetMapping("/courseSlots")
    public String listCourseSlots(Model model) {
        model.addAttribute("courseSlots", courseSlotService.findAll());
        return "staff/listCourseSlots";
    }

    @GetMapping("/courseSlots/add")
    public String showCourseSlotForm(Model model) {
        model.addAttribute("courseSlot", new CourseSlot());
        model.addAttribute("courses", courseService.findAll());
        return "staff/courseSlotForm";
    }

    @PostMapping("/courseSlots/add")
    public String addCourseSlot(@Valid @ModelAttribute("courseSlot") CourseSlot courseSlot,
                                BindingResult bindingResult,
                                Model model) {
        if (!bindingResult.hasErrors()) {
            // Associa automaticamente il trainer loggato
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Credentials credentials = credentialsService.findByUsername(userDetails.getUsername()).orElse(null);
            
            if (credentials != null && credentials.getStaff() != null) {
                courseSlot.setTrainer(credentials.getStaff());
                courseSlotService.save(courseSlot);
                return "redirect:/staff/courseSlots";
            }
        }
        model.addAttribute("courses", courseService.findAll());
        return "staff/courseSlotForm";
    }

    @GetMapping("/courseSlots/edit/{id}")
    public String showEditCourseSlotForm(@PathVariable("id") Long id, Model model) {
        CourseSlot courseSlot = courseSlotService.findById(id).orElse(null);
        if (courseSlot == null) {
            return "redirect:/staff/courseSlots";
        }
        model.addAttribute("courseSlot", courseSlot);
        model.addAttribute("courses", courseService.findAll());
        return "staff/editCourseSlotForm";
    }

    @PostMapping("/courseSlots/edit/{id}")
    public String editCourseSlot(@PathVariable("id") Long id,
                                 @Valid @ModelAttribute("courseSlot") CourseSlot updatedCourseSlot,
                                 BindingResult bindingResult,
                                 Model model) {
        if (!bindingResult.hasErrors()) {
            CourseSlot existingCourseSlot = courseSlotService.findById(id).orElse(null);
            if (existingCourseSlot != null) {
                existingCourseSlot.setCourse(updatedCourseSlot.getCourse());
                existingCourseSlot.setDayOfWeek(updatedCourseSlot.getDayOfWeek());
                existingCourseSlot.setStartTime(updatedCourseSlot.getStartTime());
                existingCourseSlot.setEndTime(updatedCourseSlot.getEndTime());
                
                courseSlotService.save(existingCourseSlot);
            }
            return "redirect:/staff/courseSlots";
        }
        model.addAttribute("courses", courseService.findAll());
        return "staff/editCourseSlotForm";
    }

    @GetMapping("/courseSlots/delete/{id}")
    public String deleteCourseSlot(@PathVariable("id") Long id) {
        courseSlotService.deleteById(id);
        return "redirect:/staff/courseSlots";
    }
    
*/
}
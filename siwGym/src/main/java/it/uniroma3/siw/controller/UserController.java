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

import java.util.Optional;

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
    private WorkoutPlanService workoutPlanService;

    @Autowired
    private WorkoutPlanDetailService workoutPlanDetailService;

    @Autowired
    private CredentialsService credentialsService;

    @Autowired
    private SubscriptionService subscriptionService;

    // --- Dashboard Utente ---
    
    @GetMapping({"", "/index"})
    public String indexUser() {
        return "user/indexUser";
    }

    // --- Gestione Profilo Utente ---

    @GetMapping("/profile")
    public String viewProfile(Model model) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Credentials credentials = credentialsService.findByUsername(userDetails.getUsername()).orElse(null);
        
        if (credentials != null) {
            model.addAttribute("user", credentials.getUser());
            return "user/profile";
        }
        return "errorPage";
    }

    @GetMapping("/profile/edit")
    public String showEditProfileForm(Model model) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Credentials credentials = credentialsService.findByUsername(userDetails.getUsername()).orElse(null);
        
        if (credentials != null) {
            model.addAttribute("user", credentials.getUser());
            return "user/editProfileForm";
        }
        return "errorPage";
    }

    @PostMapping("/profile/edit")
    public String editProfile(@Valid @ModelAttribute("user") User user,
                              BindingResult bindingResult,
                              Model model) {
        if (!bindingResult.hasErrors()) {
            userService.save(user);
            return "redirect:/user/profile";
        }
        return "user/editProfileForm";
    }

    // --- Gestione Prenotazioni (Booking) ---

    @GetMapping("/bookings")
    public String listMyBookings(Model model) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Credentials credentials = credentialsService.findByUsername(userDetails.getUsername()).orElse(null);

        if (credentials != null) {
            model.addAttribute("bookings", bookingService.findByUser(credentials.getUser()));
            return "user/myBookings";
        }
        return "errorPage";
    }

    @GetMapping("/courses/available")
    public String listAvailableCourses(Model model) {
        model.addAttribute("courses", courseService.findAll());
        return "user/availableCourses";
    }

    @GetMapping("/courses/{id}")
    public String viewCourseDetails(@PathVariable("id") Long id, Model model) {
        Course course = courseService.findById(id).orElse(null);
        if (course != null) {
            model.addAttribute("course", course);
            model.addAttribute("courseSlots", courseSlotService.findByCourse(course));
            return "user/courseDetails";
        }
        return "redirect:/user/courses/available";
    }

    @PostMapping("/courses/book/{slotId}")
    public String bookCourseSlot(@PathVariable("slotId") Long slotId, Model model) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Credentials credentials = credentialsService.findByUsername(userDetails.getUsername()).orElse(null);
        Optional<CourseSlot> slotOptional = courseSlotService.findById(slotId);

        if (credentials != null && slotOptional.isPresent()) {
            User user = credentials.getUser();
            CourseSlot courseSlot = slotOptional.get();

            // Logica di controllo: abbonamento valido e posti disponibili
            boolean hasValidSubscription = subscriptionService.isValidSubscription(user);
            boolean hasSpace = bookingService.countBookingsForSlot(courseSlot) < courseSlot.getMaxParticipants();
            boolean alreadyBooked = bookingService.isBookedByUser(courseSlot, user);

            if (hasValidSubscription && hasSpace && !alreadyBooked) {
                Booking newBooking = new Booking();
                newBooking.setUser(user);
                newBooking.setCourseSlot(courseSlot);
                bookingService.save(newBooking);
                return "redirect:/user/bookings";
            } else {
                model.addAttribute("error", "Non è possibile prenotare la lezione. Controlla l'abbonamento o i posti disponibili.");
                // Torna alla pagina dei dettagli del corso con un messaggio di errore
                return viewCourseDetails(courseSlot.getCourse().getId(), model);
            }
        }
        return "errorPage";
    }

    @GetMapping("/bookings/cancel/{bookingId}")
    public String cancelBooking(@PathVariable("bookingId") Long bookingId) {
        bookingService.deleteById(bookingId);
        return "redirect:/user/bookings";
    }

    // --- Gestione Schede Allenamento (WorkoutPlan) ---
    
    @GetMapping("/workoutPlans")
    public String listMyWorkoutPlans(Model model) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Credentials credentials = credentialsService.findByUsername(userDetails.getUsername()).orElse(null);

        if (credentials != null) {
            model.addAttribute("workoutPlans", workoutPlanService.findByUser(credentials.getUser()));
            return "user/myWorkoutPlans";
        }
        return "errorPage";
    }

    @GetMapping("/workoutPlans/{id}")
    public String viewWorkoutPlanDetails(@PathVariable("id") Long id, Model model) {
        WorkoutPlan workoutPlan = workoutPlanService.findById(id).orElse(null);
        if (workoutPlan != null) {
            model.addAttribute("workoutPlan", workoutPlan);
            model.addAttribute("details", workoutPlanDetailService.findByWorkoutPlan(workoutPlan));
            return "user/workoutPlanDetails";
        }
        return "redirect:/user/workoutPlans";
    }
}
package it.uniroma3.siw.controller;

import it.uniroma3.siw.model.Course;
import it.uniroma3.siw.model.CourseSlot;
import it.uniroma3.siw.model.Staff;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.service.CourseService;
import it.uniroma3.siw.service.CourseSlotService;
import it.uniroma3.siw.service.CredentialsService;
import it.uniroma3.siw.service.StaffService;
import it.uniroma3.siw.service.UserService;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import it.uniroma3.siw.model.Credentials;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasAnyAuthority('ADMIN')")
public class AdminController {

    @Autowired
    private CourseService courseService;

    @Autowired
    private StaffService staffService;

    @Autowired
    private UserService userService;
    
    @Autowired
    private CredentialsService credentialsService;
    
    @Autowired
    private CourseSlotService courseSlotService;
    
    // --- Pagine Dashboard Amministratore ---

    @GetMapping({"/adminDashboard"})
    public String indexAdmin() {
        return "staff/adminDashboard";
    }


    // --- Gestione Staff (Molte volte gli errori sono dati perche gli attributi nel html sono uguali al DB)---

    @GetMapping("/manageStaff")
    public String listStaff(Model model) {
        model.addAttribute("staff", staffService.findAll());
        return "staff/manageStaffFolder/manageStaff";
    }
    
    @GetMapping("/addStaff")
    public String showAddUStaffForm(Model model) {
        model.addAttribute("staff", new Staff());
        model.addAttribute("credentials", new Credentials());
        model.addAttribute("isAdmin", true);
        return "staff/manageStaffFolder/addStaff";
    }
    
    @PostMapping("/addStaff")
    public String addStaff(@Valid @ModelAttribute("staff") Staff staff,
                          BindingResult userBindingResult,
                          @Valid @ModelAttribute("credentials") Credentials credentials,
                          BindingResult credentialsBindingResult) {
        if (userBindingResult.hasErrors() || credentialsBindingResult.hasErrors()) {
            return "staff/manageStaffFolder/addStaff";
        }

        staffService.save(staff);
        credentials.setStaff(staff);
        credentialsService.save(credentials);

        return "staff/manageStaffFolder/registrationConfirmationStaff";
    }


    // --- Gestione Utenti (Molte volte gli errori sono dati perche gli attributi nel html sono uguali al DB)---

    @GetMapping("/manageUsers")
    public String listUsers(Model model) {
        model.addAttribute("users", userService.findAll());
        return "staff/manageUserFolder/manageUsers";
    }
    
    @GetMapping("/addUser")
    public String showAddUserForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("credentials", new Credentials());
        model.addAttribute("isAdmin", true);
        return "user/formRegisterUser";
    }
    
    @PostMapping("/addUser")
    public String addUser(@Valid @ModelAttribute("user") User user,
                          BindingResult userBindingResult,
                          @Valid @ModelAttribute("credentials") Credentials credentials,
                          BindingResult credentialsBindingResult) {
        if (userBindingResult.hasErrors() || credentialsBindingResult.hasErrors()) {
            return "user/formRegisterUser";
        }

        userService.save(user);
        credentials.setUser(user);
        credentials.setRole(Credentials.DEFAULT_ROLE);
        credentialsService.save(credentials);

        return "redirect:/admin/manageUsers";
    }
    
    @GetMapping("/viewUser/{id}")
    public String viewUser(@PathVariable("id") Long id, Model model) {
        Optional<User> userOptional = userService.findById(id);
        if (userOptional.isPresent()) {
            model.addAttribute("user", userOptional.get());
            return "staff/manageUserFolder/viewUser";
        }
        return "redirect:/admin/manageUsers";
    }

    @GetMapping("/editUser/{id}")
    public String showEditUserForm(@PathVariable("id") Long id, Model model) {
        Optional<User> userOptional = userService.findById(id);
        if (userOptional.isPresent()) {
            model.addAttribute("user", userOptional.get());
            return "staff/manageUserFolder/editUser";
        }
        return "redirect:/admin/manageUsers";
    }

    @PostMapping("/editUser/{id}")
    public String editUser(@PathVariable("id") Long id,
                           @Valid @ModelAttribute("user") User updatedUser,
                           BindingResult bindingResult,
                           Model model) {
        if (!bindingResult.hasErrors()) {
            userService.save(updatedUser);
            return "redirect:/admin/manageUsers";
        }
        model.addAttribute("user", updatedUser);
        return "staff/manageUserFolder/editUser";
    }

    @GetMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable("id") Long id) {
        userService.deleteById(id);
        return "redirect:/admin/manageUsers";
    }
    
    // --- Gestione Corsi ---

    @GetMapping("/manageCourses")
    public String listCourses(Model model) {
        model.addAttribute("courses", courseService.findAll());
        return "staff/manageCoursesFolder/manageCourses";
    }

    @GetMapping("/addCourse")
    public String showCourseForm(Model model) {
        model.addAttribute("course", new Course());
        return "staff/manageCoursesFolder/addCourse";
    }

    @PostMapping("/addCourse")
    public String addCourse(@Valid @ModelAttribute("course") Course course,
                            BindingResult bindingResult,
                            Model model) {
        if (!bindingResult.hasErrors()) {
            courseService.save(course);
            return "redirect:/admin/manageCourses";
        }
        return "staff/manageCoursesFolder/addCourse";
    }

    
    @GetMapping("/viewCourse/{id}")
    public String viewCourse(@PathVariable("id") Long id, Model model) {
        Optional<Course> courseOptional = courseService.findById(id);
        if (courseOptional.isPresent()) {
            model.addAttribute("course", courseOptional.get());
            return "staff/manageCoursesFolder/viewCourse";
        }
        return "redirect:/admin/manageCourses";
    }

    @GetMapping("/editCourse/{id}")
    public String showEditCourseForm(@PathVariable("id") Long id, Model model) {
        Optional<Course> courseOptional = courseService.findById(id);
        if (courseOptional.isPresent()) {
            model.addAttribute("course", courseOptional.get());
            return "staff/manageCoursesFolder/editCourse";
        }
        return "redirect:/admin/manageCourses";
    }
    
    @PostMapping("/editCourse/{id}")
    public String editCourse(@PathVariable("id") Long id,
                             @Valid @ModelAttribute("course") Course updatedCourse,
                             BindingResult bindingResult,
                             Model model) {
        if (bindingResult.hasErrors()) {
            return "staff/manageCoursesFolder/editCourse";
        }

        // 1. Recupera il corso esistente dal database usando l'ID
        Optional<Course> existingCourseOptional = courseService.findById(id);

        if (existingCourseOptional.isPresent()) {
            Course existingCourse = existingCourseOptional.get();

            // 2. Aggiorna solo i campi modificabili
            existingCourse.setName(updatedCourse.getName());
            existingCourse.setMaxCapacity(updatedCourse.getMaxCapacity());
            existingCourse.setDescription(updatedCourse.getDescription());

            // 3. Salva l'oggetto esistente e aggiornato nel database
            courseService.save(existingCourse);
        }

        return "redirect:/admin/manageCourses";
    }

    @GetMapping("/deleteCourse/{id}")
    public String deleteCourse(@PathVariable("id") Long id) {
     	courseService.deleteById(id);
        return "redirect:/admin/manageCourses";
    }

    
    // --- Gestione Slot Corsi ---
    
 /*   @GetMapping("/createCourseSlot/{courseId}")
    public String createCourseSlot(@PathVariable("courseId") Long courseId, Model model) {
        Course course = courseService.findById(courseId).orElse(null);
        if (course == null) {
            return "redirect:/admin/manageCourses"; // Gestisci il caso in cui il corso non esiste
        }

        // Qui il nome della variabile `staff` è corretto
        Iterable<Staff> staff = staffService.findTrainers(); 
        
        // Aggiungi un nuovo CourseSlot al modello
        CourseSlot courseSlot = new CourseSlot();
        courseSlot.setCourse(course);

        model.addAttribute("course", course);
        model.addAttribute("trainers", staff); // Nome `trainers` nel modello per coerenza con l'HTML
        model.addAttribute("courseSlot", courseSlot);
        
        return "staff/manageCoursesFolder/createCourseSlot";
    }
    
    @PostMapping("/courseSlots")
    public String saveCourseSlot(@Valid @ModelAttribute("courseSlot") CourseSlot courseSlot, BindingResult bindingResult, Model model) {

        // Se il trainer non è stato selezionato, aggiungi un errore manuale
        if (courseSlot.getTrainer() == null || courseSlot.getTrainer().getId() == null) {
            bindingResult.rejectValue("trainer", "trainer.required", "Selezionare un trainer è obbligatorio.");
        }

        if (bindingResult.hasErrors()) {
            // Ritorna al form con i dati e gli errori
            model.addAttribute("course", courseSlot.getCourse());
            model.addAttribute("trainers", staffService.findTrainers());
            return "staff/manageCoursesFolder/createCourseSlot";
        }

        // Salva il CourseSlot
        courseSlotService.save(courseSlot);
        
        // Reindirizza l'utente alla pagina dei dettagli del corso
        return "redirect:/admin/viewCourse/" + courseSlot.getCourse().getId();
    }*/
    
}
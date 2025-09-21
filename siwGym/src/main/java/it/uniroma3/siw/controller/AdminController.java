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
    public String indexAdmin(Model model) {
        
        // Calcola il numero totale di utenti registrati
        long userCount = userService.count();
        
        // Calcola il numero totale di membri dello staff
        long staffCount = staffService.count();
        
        // Calcola il numero di admin (assumendo che il ruolo sia 'ADMIN')
        long adminCount = credentialsService.countByRole("ADMIN"); 
        
        // Calcola il numero di trainer (assumendo che il ruolo sia 'TRAINER' o simile)
        long trainerCount = credentialsService.countByRole("TRAINER");
        
        // Calcola il numero totale di corsi (assumendo che tutti i corsi esistenti siano "attivi")
        long courseCount = courseService.count();
        
        // Calcola il numero totale di slot attivi di tutti i corsi
        long courseSlotCount = courseSlotService.count();

        // Aggiungi gli attributi al modello
        model.addAttribute("userCount", userCount);
        model.addAttribute("staffCount", staffCount);
        model.addAttribute("adminCount", adminCount);
        model.addAttribute("trainerCount", trainerCount);
        model.addAttribute("courseCount", courseCount);
        model.addAttribute("courseSlotCount", courseSlotCount);

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

    
    @GetMapping("/viewStaff/{id}")
    public String viewStaff(@PathVariable("id") Long id, Model model) {
        Optional<Staff> staffOptional = staffService.findById(id);
        if (staffOptional.isPresent()) {
            model.addAttribute("staff", staffOptional.get());
            return "staff/manageStaffFolder/viewStaff";
        }
        return "redirect:/admin/manageStaff";
    }
    
    
    @GetMapping("/editStaff/{id}")
    public String showEditStaffForm(@PathVariable("id") Long id, Model model) {
        Optional<Staff> staffOptional = staffService.findById(id);
        if (staffOptional.isEmpty()) {
            return "redirect:/admin/manageStaff";
        }
        model.addAttribute("staff", staffOptional.get());
        return "staff/manageStaffFolder/editStaff"; // Corretto il percorso del template
    }

    @PostMapping("/editStaff/{id}")
    public String editStaff(@PathVariable("id") Long id,
                            @Valid @ModelAttribute("staff") Staff updatedStaff,
                            BindingResult bindingResult,
                            Model model) {
        if (!bindingResult.hasErrors()) {
            Staff existingStaff = staffService.findById(id).orElse(null);
            if (existingStaff != null) {
                existingStaff.setName(updatedStaff.getName());
                existingStaff.setSurname(updatedStaff.getSurname());
                existingStaff.setEmail(updatedStaff.getEmail());
                existingStaff.setSpecialization(updatedStaff.getSpecialization());
                staffService.save(existingStaff);
            }
            return "redirect:/admin/manageStaff";
        }
        return "staff/manageStaffFolder/editStaff"; // Corretto il percorso del template
    }
    	
    
    @GetMapping("/deleteStaff/{id}")
    public String deleteStaff(@PathVariable("id") Long id) {
        Optional<Staff> staffOptional = staffService.findById(id);
        if (staffOptional.isPresent()) {
            Staff staffToDelete = staffOptional.get();

            // 1. Elimina le credenziali associate al membro dello staff
            Optional<Credentials> credentialsOptional = credentialsService.findByStaff(staffToDelete);
            credentialsOptional.ifPresent(credentialsService::delete);
            
            // 2. Elimina gli slot corso associati al membro dello staff
            List<CourseSlot> courseSlots = courseSlotService.findByTrainer(staffToDelete);
            courseSlotService.deleteAll(courseSlots);

            // 3. Ora è possibile eliminare l'entità Staff
            staffService.deleteById(id);
        }
        return "redirect:/admin/manageStaff";
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
    
    // Metodo GET per mostrare il form di creazione dello slot
    @GetMapping("/createCourseSlot/{courseId}")
    public String createCourseSlot(@PathVariable("courseId") Long courseId, Model model) {
        Optional<Course> courseOptional = courseService.findById(courseId);
        
        if (courseOptional.isEmpty()) {
            return "redirect:/admin/manageCourses";
        }
        
        Course course = courseOptional.get();

        Iterable<Staff> trainers = staffService.findTrainers();
        
        CourseSlot courseSlot = new CourseSlot();
        courseSlot.setCourse(course);

        model.addAttribute("course", course);
        model.addAttribute("trainers", trainers);
        model.addAttribute("courseSlot", courseSlot);
        
        return "staff/manageCoursesFolder/createCourseSlot";
    }

    // Metodo POST per salvare il nuovo slot
    @PostMapping("/courseSlots")
    public String saveCourseSlot(@Valid @ModelAttribute("courseSlot") CourseSlot courseSlot, BindingResult bindingResult, Model model) {
        
        // Verifica se l'ID dello staff è stato selezionato dal form
        if (courseSlot.getTrainer() == null || courseSlot.getTrainer().getId() == null) {
            bindingResult.rejectValue("trainer", "trainer.required", "Selezionare un membro dello staff è obbligatorio.");
        }
        
        if (bindingResult.hasErrors()) {
            model.addAttribute("course", courseSlot.getCourse());
            model.addAttribute("trainers", staffService.findAll());
            return "staff/manageCoursesFolder/createCourseSlot";
        }
        
        // Carica l'entità Staff completa dal database usando l'ID ricevuto dal form
        Optional<Staff> trainerOptional = staffService.findById(courseSlot.getTrainer().getId());
        if (trainerOptional.isPresent()) {
            // Se lo staff è stato trovato, impostalo sull'oggetto courseSlot
            courseSlot.setTrainer(trainerOptional.get());
        } else {
            // Gestisci l'errore se lo staff non viene trovato
            return "redirect:/error"; 
        }
        
        // Salva lo slot, che ora ha l'entità Staff completa
        courseSlotService.save(courseSlot);
        
        return "redirect:/admin/viewCourse/" + courseSlot.getCourse().getId();
    }
    
 // Metodo GET per mostrare il form di modifica dello slot
    @GetMapping("/editCourseSlot/{id}")
    public String showEditCourseSlotForm(@PathVariable("id") Long id, Model model) {
        // Carica lo slot da modificare
        Optional<CourseSlot> courseSlotOptional = courseSlotService.findById(id);
        
        if (courseSlotOptional.isEmpty()) {
            // Se lo slot non esiste, reindirizza
            return "redirect:/admin/manageCourses"; // O una pagina di errore
        }
        
        CourseSlot courseSlot = courseSlotOptional.get();

        // Passa la lista dei trainer (o di tutto lo staff) al modello
        Iterable<Staff> trainers = staffService.findTrainers();
        
        model.addAttribute("courseSlot", courseSlot);
        model.addAttribute("trainers", trainers);
        
        return "staff/manageCoursesFolder/editCourseSlot";
    }
    
    
    @PostMapping("/editCourseSlot/{id}")
    public String editCourseSlot(@PathVariable("id") Long id,
                                 @Valid @ModelAttribute("courseSlot") CourseSlot updatedCourseSlot,
                                 BindingResult bindingResult,
                                 Model model) {

        // Prima di tutto, verifica se ci sono errori di validazione del form
        if (bindingResult.hasErrors()) {
            model.addAttribute("trainers", staffService.findTrainers());
            return "staff/manageCoursesFolder/editCourseSlot";
        }
        
        // Cerca lo slot del corso esistente nel database
        Optional<CourseSlot> existingCourseSlotOptional = courseSlotService.findById(id);

        if (existingCourseSlotOptional.isPresent()) {
            CourseSlot existingCourseSlot = existingCourseSlotOptional.get();

            // 1. Carica l'entità Staff completa usando l'ID ricevuto dal form.
            // Il form invia l'ID del trainer, che si trova in updatedCourseSlot.getTrainer().getId()
            Optional<Staff> trainerOptional = staffService.findById(updatedCourseSlot.getTrainer().getId());

            if (trainerOptional.isPresent()) {
                // 2. Aggiorna i campi dello slot esistente con i dati del form.
                // Imposta l'oggetto Staff completo che hai appena caricato.
                existingCourseSlot.setTrainer(trainerOptional.get());
                existingCourseSlot.setDayOfWeek(updatedCourseSlot.getDayOfWeek());
                existingCourseSlot.setStartTime(updatedCourseSlot.getStartTime());
                existingCourseSlot.setEndTime(updatedCourseSlot.getEndTime());
                existingCourseSlot.setMaxParticipants(updatedCourseSlot.getMaxParticipants());

                // 3. Salva l'oggetto esistente e aggiornato nel database
                courseSlotService.save(existingCourseSlot);
            
                // Reindirizza alla pagina del corso
                return "redirect:/admin/viewCourse/" + existingCourseSlot.getCourse().getId();
            } else {
                // Gestisci il caso in cui il trainer non viene trovato nel database
                // (es. l'ID inviato dal form non esiste)
                // Aggiungi un errore per informare l'utente
                bindingResult.rejectValue("trainer", "trainer.notFound", "Trainer non trovato.");
                model.addAttribute("trainers", staffService.findTrainers());
                return "staff/manageCoursesFolder/editCourseSlot";
            }
        }

        // Se lo slot non esiste, reindirizza a una pagina di gestione
        return "redirect:/admin/manageCourses";
    }
    
    
    @GetMapping("/deleteCourseSlot/{id}")
    public String deleteCourseSlot(@PathVariable("id") Long id, Model model) {
        // Cerca lo slot del corso per ottenere l'ID del corso prima di eliminarlo
        Optional<CourseSlot> courseSlotOptional = courseSlotService.findById(id);

        if (courseSlotOptional.isPresent()) {
            // Ottieni l'ID del corso per reindirizzare alla pagina corretta
            Long courseId = courseSlotOptional.get().getCourse().getId();

            // Elimina lo slot del corso dal database
            courseSlotService.deleteById(id);
            
            // Reindirizza alla pagina del corso per mostrare lo stato aggiornato
            return "redirect:/admin/viewCourse/" + courseId;
        }

        // Se lo slot non esiste, reindirizza a una pagina di gestione
        return "redirect:/admin/manageCourses";
    }
}
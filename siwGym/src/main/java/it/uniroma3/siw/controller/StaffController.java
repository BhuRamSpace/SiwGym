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

@Controller
@RequestMapping("/staff")
@PreAuthorize("hasAnyAuthority('ADMIN', 'TRAINER')")
public class StaffController {

    @Autowired
    private StaffService staffService;
    
    @Autowired
    private CourseService courseService;

    @Autowired
    private CourseSlotService courseSlotService;

    @Autowired
    private UserService userService;
    
    @Autowired
    private WorkoutPlanService workoutPlanService;

    @Autowired
    private WorkoutPlanDetailService workoutPlanDetailService;
    
    @Autowired
    private ExerciseService exerciseService;

    @Autowired
    private CredentialsService credentialsService;

    // --- Dashboard Staff ---
    
    @GetMapping({"", "/index"})
    public String indexStaff() {
        return "staff/indexStaff";
    }

    // --- Gestione Lezioni (CourseSlot) ---

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
    
    // --- Gestione Schede Allenamento (WorkoutPlan) ---

    @GetMapping("/workoutPlans")
    public String listWorkoutPlans(Model model) {
        model.addAttribute("workoutPlans", workoutPlanService.findAll());
        return "staff/listWorkoutPlans";
    }

    @GetMapping("/workoutPlans/add")
    public String showWorkoutPlanForm(Model model) {
        model.addAttribute("workoutPlan", new WorkoutPlan());
        model.addAttribute("users", userService.findAll());
        model.addAttribute("exercises", exerciseService.findAll());
        return "staff/workoutPlanForm";
    }

    @PostMapping("/workoutPlans/add")
    public String addWorkoutPlan(@Valid @ModelAttribute("workoutPlan") WorkoutPlan workoutPlan,
                                BindingResult bindingResult,
                                Model model) {
        if (!bindingResult.hasErrors()) {
            // Associa automaticamente il trainer loggato
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Credentials credentials = credentialsService.findByUsername(userDetails.getUsername()).orElse(null);
            
            if (credentials != null && credentials.getStaff() != null) {
                workoutPlan.setTrainer(credentials.getStaff());
                workoutPlanService.save(workoutPlan);
       
                return "redirect:/staff/workoutPlans"+ workoutPlan.getId();
            }
        }
        model.addAttribute("users", userService.findAll());
        model.addAttribute("exercises", exerciseService.findAll());
        return "staff/workoutPlanForm";
    }

    @GetMapping("/workoutPlans/edit/{id}")
    public String showEditWorkoutPlanForm(@PathVariable("id") Long id, Model model) {
        WorkoutPlan workoutPlan = workoutPlanService.findById(id).orElse(null);
        if (workoutPlan == null) {
            return "redirect:/staff/workoutPlans";
        }
        model.addAttribute("workoutPlan", workoutPlan);
        model.addAttribute("users", userService.findAll());
        model.addAttribute("exercises", exerciseService.findAll());
        return "staff/editWorkoutPlanForm";
    }

    @PostMapping("/workoutPlans/edit/{id}")
    public String editWorkoutPlan(@PathVariable("id") Long id,
                                 @Valid @ModelAttribute("workoutPlan") WorkoutPlan updatedWorkoutPlan,
                                 BindingResult bindingResult,
                                 Model model) {
        if (!bindingResult.hasErrors()) {
            WorkoutPlan existingWorkoutPlan = workoutPlanService.findById(id).orElse(null);
            if (existingWorkoutPlan != null) {
                existingWorkoutPlan.setUser(updatedWorkoutPlan.getUser());
                existingWorkoutPlan.setName(updatedWorkoutPlan.getName());
                // Non aggiornare il trainer, che resta quello che ha creato la scheda
                workoutPlanService.save(existingWorkoutPlan);
            }
            return "redirect:/staff/workoutPlans";
        }
        model.addAttribute("users", userService.findAll());
        model.addAttribute("exercises", exerciseService.findAll());
        return "staff/editWorkoutPlanForm";
    }

    @GetMapping("/workoutPlans/delete/{id}")
    public String deleteWorkoutPlan(@PathVariable("id") Long id) {
        workoutPlanService.deleteById(id);
        return "redirect:/staff/workoutPlans";
    }
    
 // --- Gestione Dettagli Scheda Allenamento (WorkoutPlanDetail) ---

    // Metodo per visualizzare gli esercizi di una specifica scheda di allenamento
    @GetMapping("/workoutPlans/{planId}")
    public String viewWorkoutPlanDetails(@PathVariable("planId") Long planId, Model model) {
        WorkoutPlan workoutPlan = workoutPlanService.findById(planId).orElse(null);
        if (workoutPlan == null) {
            return "redirect:/staff/workoutPlans"; // Torna alla lista se la scheda non esiste
        }
        model.addAttribute("workoutPlan", workoutPlan);
        model.addAttribute("details", workoutPlanDetailService.findByWorkoutPlan(workoutPlan));
        return "staff/viewWorkoutPlanDetails";
    }

    // Metodo GET per mostrare il form di aggiunta di un esercizio a una scheda
    @GetMapping("/workoutPlans/{planId}/details/add")
    public String showWorkoutPlanDetailForm(@PathVariable("planId") Long planId, Model model) {
        WorkoutPlan workoutPlan = workoutPlanService.findById(planId).orElse(null);
        if (workoutPlan == null) {
            return "redirect:/staff/workoutPlans";
        }
        model.addAttribute("workoutPlan", workoutPlan);
        model.addAttribute("workoutPlanDetail", new WorkoutPlanDetail());
        model.addAttribute("exercises", exerciseService.findAll());
        return "staff/workoutPlanDetailForm";
    }

    // Metodo POST per aggiungere un esercizio alla scheda di allenamento
    @PostMapping("/workoutPlans/{planId}/details/add")
    public String addWorkoutPlanDetail(@PathVariable("planId") Long planId,
                                       @Valid @ModelAttribute("workoutPlanDetail") WorkoutPlanDetail workoutPlanDetail,
                                       BindingResult bindingResult,
                                       Model model) {
        WorkoutPlan workoutPlan = workoutPlanService.findById(planId).orElse(null);
        if (workoutPlan == null) {
            return "redirect:/staff/workoutPlans";
        }

        if (!bindingResult.hasErrors()) {
            workoutPlanDetail.setWorkoutPlan(workoutPlan);
            workoutPlanDetailService.save(workoutPlanDetail);
            return "redirect:/staff/workoutPlans/" + planId; // Torna alla visualizzazione della scheda
        }
        
        model.addAttribute("workoutPlan", workoutPlan);
        model.addAttribute("exercises", exerciseService.findAll());
        return "staff/workoutPlanDetailForm";
    }

    // Metodo per eliminare un dettaglio (esercizio) da una scheda di allenamento
    @GetMapping("/workoutPlans/{planId}/details/delete/{detailId}")
    public String deleteWorkoutPlanDetail(@PathVariable("planId") Long planId, @PathVariable("detailId") Long detailId) {
        workoutPlanDetailService.deleteById(detailId);
        return "redirect:/staff/workoutPlans/" + planId;
    }
}
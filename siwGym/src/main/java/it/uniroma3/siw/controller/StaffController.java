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
}
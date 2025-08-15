package it.uniroma3.siw.controller;

import it.uniroma3.siw.model.Course;
import it.uniroma3.siw.model.Staff;
import it.uniroma3.siw.model.Subscription;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.service.CourseService;
import it.uniroma3.siw.service.CredentialsService;
import it.uniroma3.siw.service.StaffService;
import it.uniroma3.siw.service.SubscriptionService;
import it.uniroma3.siw.service.UserService;
import jakarta.validation.Valid;

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
    private SubscriptionService subscriptionService;
    
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
}
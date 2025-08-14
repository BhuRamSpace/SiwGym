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

    // --- Gestione Corsi ---

    @GetMapping("/manageCourses")
    public String listCourses(Model model) {
        model.addAttribute("courses", courseService.findAll());
        return "staff/manageCourses";
    }

    @GetMapping("/courses/add")
    public String showCourseForm(Model model) {
        model.addAttribute("course", new Course());
        return "staff/courseForm";
    }

    @PostMapping("/courses/add")
    public String addCourse(@Valid @ModelAttribute("course") Course course,
                            BindingResult bindingResult,
                            Model model) {
        if (!bindingResult.hasErrors()) {
            courseService.save(course);
            return "redirect:/admin/manageCourses";
        }
        return "staff/courseForm";
    }

    @GetMapping("/courses/delete/{id}")
    public String deleteCourse(@PathVariable("id") Long id) {
        courseService.deleteById(id);
        return "redirect:/admin/manageCourses";
    }

    // --- Gestione Staff ---

    @GetMapping("/manageStaff")
    public String listStaff(Model model) {
        model.addAttribute("staff", staffService.findAll());
        return "staff/manageStaff";
    }

    @GetMapping("/staff/add")
    public String showStaffForm(Model model) {
        model.addAttribute("staffMember", new Staff());
        model.addAttribute("credentials", new Credentials());
        return "staff/staffForm";
    }

    @PostMapping("/staff/add")
    public String addStaff(@Valid @ModelAttribute("staffMember") Staff staff,
                           BindingResult staffBindingResult,
                           @Valid @ModelAttribute("credentials") Credentials credentials,
                           BindingResult credentialsBindingResult,
                           Model model) {
        if (!staffBindingResult.hasErrors() && !credentialsBindingResult.hasErrors()) {
            staffService.save(staff);
            credentials.setStaff(staff);
            credentials.setRole(Credentials.TRAINER_ROLE);
            credentialsService.save(credentials);
            return "redirect:/admin/manageStaff";
        }
        return "staff/staffForm";
    }

    @GetMapping("/staff/delete/{id}")
    public String deleteStaff(@PathVariable("id") Long id) {
        staffService.deleteById(id);
        return "redirect:/admin/manageStaff";
    }

    // --- Gestione Utenti e Abbonamenti ---

    @GetMapping("/manageUsers")
    public String listUsers(Model model) {
        model.addAttribute("users", userService.findAll());
        return "staff/manageUsers";
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
            return "staff/viewUser";
        }
        return "redirect:/admin/manageUsers";
    }

    @GetMapping("/editUser/{id}")
    public String showEditUserForm(@PathVariable("id") Long id, Model model) {
        Optional<User> userOptional = userService.findById(id);
        if (userOptional.isPresent()) {
            model.addAttribute("user", userOptional.get());
            return "staff/editUser";
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
        return "staff/editUser";
    }

    @GetMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable("id") Long id) {
        userService.deleteById(id);
        return "redirect:/admin/manageUsers";
    }

    @GetMapping("/manageSubscriptions")
    public String listSubscriptions(Model model) {
        model.addAttribute("subscriptions", subscriptionService.findAll());
        return "staff/manageSubscriptions";
    }

    @GetMapping("/subscriptions/add/{userId}")
    public String showSubscriptionForm(@PathVariable("userId") Long userId, Model model) {
        Optional<User> userOptional = userService.findById(userId);
        if (userOptional.isPresent()) {
            model.addAttribute("user", userOptional.get());
            model.addAttribute("subscription", new Subscription());
            return "staff/subscriptionForm";
        }
        return "redirect:/admin/manageUsers";
    }

    @PostMapping("/subscriptions/add/{userId}")
    public String addSubscription(@PathVariable("userId") Long userId,
                                  @Valid @ModelAttribute("subscription") Subscription subscription,
                                  BindingResult bindingResult,
                                  Model model) {
        Optional<User> userOptional = userService.findById(userId);
        if (!bindingResult.hasErrors() && userOptional.isPresent()) {
            User user = userOptional.get();
            subscription.setUser(user);
            subscriptionService.save(subscription);
            return "redirect:/admin/manageSubscriptions";
        }
        model.addAttribute("user", userOptional.orElse(null));
        return "staff/subscriptionForm";
    }

    @GetMapping("/subscriptions/delete/{id}")
    public String deleteSubscription(@PathVariable("id") Long id) {
        subscriptionService.deleteById(id);
        return "redirect:/admin/manageSubscriptions";
    }
}
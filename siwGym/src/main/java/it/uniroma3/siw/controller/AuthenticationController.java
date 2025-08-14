package it.uniroma3.siw.controller;

import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.service.CredentialsService;
import it.uniroma3.siw.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthenticationController {

    @Autowired
    private CredentialsService credentialsService;

    @Autowired
    private UserService userService;
    
    /**
     * Gestisce la richiesta GET per la pagina di registrazione.
     * Restituisce il form di registrazione per un nuovo utente.
     */
    @GetMapping(value = "/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("credentials", new Credentials());
        return "user/formRegisterUser";
    }

    /**
     * Gestisce la richiesta GET per la pagina di login.
     * Restituisce il form di login.
     */
    @GetMapping(value = "/login")
    public String showLoginForm(Model model) {
        return "formLogin";
    }

    /**
     * Gestisce il reindirizzamento dopo un login riuscito.
     * Reindirizza l'utente alla dashboard appropriata in base al suo ruolo.
     */
    @GetMapping(value = "/success")
    public String defaultAfterLogin(Model model) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Credentials credentials = credentialsService.findByUsername(userDetails.getUsername()).orElse(null);
        
        if (credentials != null) {
            if (credentials.getRole().equals(Credentials.ADMIN_ROLE)) {
                return "staff/adminDashboard";
            }
            if (credentials.getRole().equals(Credentials.TRAINER_ROLE)) {
                return "staff/trainerDashboard";
            }
        }
        return "user/userDashboard";

    }

    /**
     * Gestisce la richiesta POST per la registrazione di un nuovo utente.
     * Salva l'utente e le sue credenziali se i dati sono validi.
     */
    
    @PostMapping(value = "/register")
    public String registerUser(@Valid @ModelAttribute("user") User user,
                               BindingResult userBindingResult,
                               @Valid @ModelAttribute("credentials") Credentials credentials,
                               BindingResult credentialsBindingResult,
                               Model model) {

        // Controllo username duplicato
        if (credentialsService.findByUsername(credentials.getUsername()).isPresent()) {
            model.addAttribute("usernameDuplicate", "Questo username è già in uso.");
            return "user/formRegisterUser";
        }

        if (!userBindingResult.hasErrors() && !credentialsBindingResult.hasErrors()) {
            userService.save(user);
            credentials.setUser(user);

            // Ruolo di default
            if (credentials.getRole() == null || credentials.getRole().isBlank()) {
                credentials.setRole(Credentials.DEFAULT_ROLE);
            }

            credentialsService.save(credentials);

            // 🔹 Recupero utente loggato
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            boolean isAdmin = false;
            if (principal instanceof UserDetails) {
                String username = ((UserDetails) principal).getUsername();
                Credentials currentCreds = credentialsService.findByUsername(username).orElse(null);
                if (currentCreds != null && Credentials.ADMIN_ROLE.equals(currentCreds.getRole())) {
                    isAdmin = true;
                }
            }

            // Passo variabili alla view
            model.addAttribute("user", user);
            model.addAttribute("registrationSuccess", true);
            model.addAttribute("isAdmin", isAdmin);

            return "registrationConfirmation";
        }

        return "user/formRegisterUser";
    }


}
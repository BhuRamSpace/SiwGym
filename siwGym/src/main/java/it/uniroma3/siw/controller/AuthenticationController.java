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
        return "formRegisterUser";
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
                return "admin/indexAdmin";
            }
            if (credentials.getRole().equals(Credentials.TRAINER_ROLE)) {
                return "staff/indexStaff";
            }
        }
        return "user/indexUser";

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
                               
        // Controlla se l'username è già in uso prima di procedere
        if (credentialsService.findByUsername(credentials.getUsername()).isPresent()) {
            model.addAttribute("usernameDuplicate", "Questo username è già in uso.");
            return "formRegisterUser";
        }
        // Se user e credentials hanno entrambi contenuti validi, memorizza User e Credentials nel DB
        if (!userBindingResult.hasErrors() && !credentialsBindingResult.hasErrors()) {
            userService.save(user);
            credentials.setUser(user);
            credentialsService.save(credentials);
            model.addAttribute("user", user);
            model.addAttribute("registrationSuccess", true);
            return "registrationConfirmation";
        }
        // Se ci sono errori, torna al form di registrazione
        return "formRegisterUser";
   	}
}
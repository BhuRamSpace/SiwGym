package it.uniroma3.siw.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    /**
     * Questo metodo gestisce la richiesta per la pagina principale del sito.
     * È la pagina che viene mostrata a tutti gli utenti, inclusi quelli non autenticati.
     */
    @GetMapping("/")
    public String index(Model model) {
        return "index";
    }

    /**
     * Un esempio di un'altra pagina pubblica, come una pagina "Chi siamo".
     *
     */
    @GetMapping("/about")
    public String aboutUs() {
        return "about";
    }
}
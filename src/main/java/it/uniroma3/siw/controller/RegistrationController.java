package it.uniroma3.siw.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.service.CredentialsService;
import it.uniroma3.siw.service.UserService;

@Controller
public class RegistrationController {

    private CredentialsService credentialsService;
    private UserService userService;

    public RegistrationController(CredentialsService credentialsService, UserService userService) {
        this.credentialsService = credentialsService;
        this.userService = userService;
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("credentials", new Credentials());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute("credentials") Credentials credentials, Model model) {
        if (this.credentialsService.getCredentials(credentials.getUsername()) != null) {
            model.addAttribute("errorMessage", "Username già esistente");
            return "register";
        }

        User user = new User();
        user.setUsername(credentials.getUsername());

        credentials.setRole(Credentials.DEFAULT_ROLE);
        credentials.setUser(user);

        this.userService.saveUser(user);
        this.credentialsService.saveCredentials(credentials);

        return "redirect:/login?registered=true";
    }
}
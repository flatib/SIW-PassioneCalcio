package it.uniroma3.siw.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.service.CredentialsService;
import it.uniroma3.siw.service.UserService;

@Configuration
public class AdminBootstrapper {

	@Bean
	public CommandLineRunner setupDefaultAdmin(CredentialsService credentialsService, UserService userService) {
        return args -> {
            if (!credentialsService.existsByUsername("admin")) {
                User adminUser = new User();
                adminUser.setUsername("admin");
                Credentials adminCreds = new Credentials();
                adminCreds.setUsername("admin");
                adminCreds.setPassword("password");
                adminCreds.setUser(adminUser);
                credentialsService.saveAdminCredentials(adminCreds);
                System.out.println("Utente ADMIN di default creato");
            }
        };
    }
}

package it.uniroma3.siw.controller.rest;

import it.uniroma3.siw.repository.TeamRepository;
import it.uniroma3.siw.service.TeamService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test-performance")
public class PerformanceTestController {

    private TeamService teamService;
    private TeamRepository teamRepository;
    
    public PerformanceTestController(TeamService teamService, TeamRepository teamRepository) {
		this.teamService = teamService;
		this.teamRepository = teamRepository;
	}

    @GetMapping("/bad")
    public String testBad() {
        teamService.unoptimizedFetch();
        return "Test N+1 completato. Guarda la console di Spring Boot!";
    }

    @GetMapping("/good")
    public String testGood() {
        teamService.optimizedFetch();
        return "Test Ottimizzato completato. Guarda la console di Spring Boot!";
    }
    
    @GetMapping("/test")
    public String runTest() {
        long badTime = teamService.unoptimizedFetch();
        long goodTime = teamService.optimizedFetch();
        
        return String.format("Risultati Test (su %d squadre):\n" +
                             "Strategia N+1 (Lazy): %d ms\n" +
                             "Strategia Ottimizzata (Join Fetch): %d ms", 
                             teamRepository.count(), badTime, goodTime);
    }
}
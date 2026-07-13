package it.uniroma3.siw.controller.rest;

import it.uniroma3.siw.model.Tournament;
import it.uniroma3.siw.service.TournamentService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/rest/tournaments")
public class TournamentRestController {

    private TournamentService tournamentService;
    
    public TournamentRestController(TournamentService tournamentService) {
		this.tournamentService = tournamentService;
	}

    @GetMapping
    public List<Tournament> searchTournaments(
            @RequestParam(required = false) String name) {
        
        return tournamentService.searchTournaments(name);
    }
}
package it.uniroma3.siw.controller.rest;

import it.uniroma3.siw.model.Match;
import it.uniroma3.siw.service.MatchService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rest/matches")
public class MatchRestController {

    private MatchService matchService;
    
    public MatchRestController(MatchService matchService) {
		this.matchService = matchService;
	}

    @GetMapping
    public List<Match> getMatches(@RequestParam(required = false) String tournament) {
        return matchService.searchMatchesByTournament(tournament);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMatch(@PathVariable Long id) {
        matchService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
package it.uniroma3.siw.controller.rest;

import it.uniroma3.siw.model.Match;
import it.uniroma3.siw.service.MatchService;

import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/rest/matches")
public class MatchRestController {

    private MatchService matchService;
    
    public MatchRestController(MatchService matchService) {
		this.matchService = matchService;
	}

    @GetMapping
    public Page<Match> getMatches(@RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "10") int size,
                                  @RequestParam(required = false) String search,
                                  @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                  @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return matchService.searchMatchesPaginated(search, startDate, endDate, page, size);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMatch(@PathVariable Long id) {
        matchService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
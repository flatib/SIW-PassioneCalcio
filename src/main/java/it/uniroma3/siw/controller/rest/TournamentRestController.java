package it.uniroma3.siw.controller.rest;

import it.uniroma3.siw.model.Image;
import it.uniroma3.siw.model.Tournament;
import it.uniroma3.siw.model.dto.TeamOption;
import it.uniroma3.siw.service.TournamentService;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/rest/tournaments")
public class TournamentRestController {

    private TournamentService tournamentService;
    
    public TournamentRestController(TournamentService tournamentService) {
		this.tournamentService = tournamentService;
	}

    @GetMapping
    public Page<Tournament> getTournaments(@RequestParam(defaultValue = "0") int page,
									   @RequestParam(defaultValue = "12") int size,
									   @RequestParam(required = false) String search,
									   @RequestParam(required = false) Integer year) {
		return tournamentService.searchTournamentsPaginated(search, year, page, size);
	}
    
    @GetMapping("/{id}/image")
	public ResponseEntity<byte[]> getImmagine(@PathVariable("id") Long id) {
		Tournament tournament = tournamentService.findById(id);
		Image img = tournament.getImage();
		if (img == null) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok().contentType(MediaType.parseMediaType(img.getContentType())).body(img.getDati());
	}
    
    @GetMapping("/{id}/teams")
    public List<TeamOption> getTournamentTeams(@PathVariable("id") Long id) {
        return tournamentService.findTeamsByTournamentId(id).stream()
                .map(team -> new TeamOption(team.getId(), team.getName()))
                .collect(Collectors.toList());
    }
    
    @GetMapping("/years")
	public List<Integer> getYears() {
		return tournamentService.getAvailableYears();
	}
}
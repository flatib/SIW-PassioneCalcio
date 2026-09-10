package it.uniroma3.siw.controller.rest;

import it.uniroma3.siw.model.Image;
import it.uniroma3.siw.model.Team;
import it.uniroma3.siw.service.TeamService;

import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rest/teams")
public class TeamRestController {

    private TeamService teamService;
    
    public TeamRestController(TeamService teamService) {
		this.teamService = teamService;
	}

    @GetMapping
    public Page<Team> getTeams(@RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "12") int size,
                               @RequestParam(required = false) String search,
                               @RequestParam(required = false) Integer year,
                               @RequestParam(required = false) String city) {
        return teamService.searchTeamsPaginated(search, year, city, page, size);
    }
    
    @GetMapping("/{id}/image")
	public ResponseEntity<byte[]> getImmagine(@PathVariable("id") Long id) {
		Team team = teamService.findById(id);
		Image img = team.getImage();
		if (img == null) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok().contentType(MediaType.parseMediaType(img.getContentType())).body(img.getDati());
	}

    @GetMapping("/cities")
    public List<String> getCities() {
        return teamService.getAvailableCities();
    }

    @GetMapping("/years")
    public List<Integer> getYears() {
        return teamService.getAvailableYears();
    }
}
package it.uniroma3.siw.controller.rest;

import it.uniroma3.siw.model.Team;
import it.uniroma3.siw.service.TeamService;
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
    public List<Team> getTeams(@RequestParam(required = false) String name) {
        return teamService.searchTeams(name);
    }
}
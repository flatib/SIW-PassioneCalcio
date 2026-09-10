package it.uniroma3.siw.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import it.uniroma3.siw.model.Match;
import it.uniroma3.siw.model.Team;
import it.uniroma3.siw.service.MatchService;
import it.uniroma3.siw.service.TeamService;

@Controller
@RequestMapping("/teams")
public class TeamController {

	private final TeamService teamService;
	private final MatchService matchService;
	
	public TeamController(TeamService teamService, MatchService matchService) {
		this.teamService = teamService;
		this.matchService = matchService;
	}
	
	@GetMapping("/{id}")
	public String showTeam(@PathVariable("id") Long id, Model model) {
	    Team team = this.teamService.findById(id);

	    if (team == null) {
	    	return "redirect:/tournaments";
	    }

	    List<Match> teamMatches = this.matchService.findMatchesByTeamOrdered(id);

	    Map<LocalDate, List<Match>> teamMatchesByDate = teamMatches.stream()
	    		.collect(Collectors.groupingBy(
	    				match -> match.getDateAndTime().toLocalDate(),
	    				TreeMap::new,
	    				Collectors.toList()
	    		));

	    model.addAttribute("team", team);
	    model.addAttribute("matchesByDate", teamMatchesByDate);

		return "teams/detail";
	}
	
	@GetMapping
    public String listTeams(Model model) {
        model.addAttribute("teams", this.teamService.findAll());
        return "teams/list";
    }
}
package it.uniroma3.siw.controller;

import java.time.LocalDate;
import java.util.Comparator;
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
import it.uniroma3.siw.model.Tournament;
import it.uniroma3.siw.model.dto.StandingRow;
import it.uniroma3.siw.service.TournamentService;

@Controller
@RequestMapping("/tournaments")
public class TournamentController {

	private final TournamentService tournamentService;
	
	public TournamentController(TournamentService tournamentService) {
		this.tournamentService = tournamentService;
	}
	
	@GetMapping("/{id}")
	public String show(@PathVariable("id") Long id, Model model) {
		Tournament tournament = this.tournamentService.findById(id);

		if (tournament == null) {
			return "redirect:/tournaments";
		}

	    Map<LocalDate, List<Match>> matchesByDate = tournament.getMatches().stream()
	        .collect(Collectors.groupingBy(
	            match -> match.getDateAndTime().toLocalDate(),
	            TreeMap::new,
	            Collectors.toList()
	        ));

	    matchesByDate.values().forEach(list ->
	        list.sort(Comparator.comparing(Match::getDateAndTime))
	    );

	    List<StandingRow> standings = this.tournamentService.calculateStandings(tournament);

	    model.addAttribute("standings", standings);
	    model.addAttribute("tournament", tournament);
	    model.addAttribute("matchesByDate", matchesByDate);
		return "tournaments/detail";
	}
	
	@GetMapping
	public String list(Model model) {
		List<Tournament> tournaments = this.tournamentService.findAll();
		model.addAttribute("tournaments", tournaments);
		return "tournaments/list";
	}
}
package it.uniroma3.siw.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import it.uniroma3.siw.model.Match;
import it.uniroma3.siw.model.Team;
import it.uniroma3.siw.model.Tournament;
import it.uniroma3.siw.model.dto.StandingRow;
import it.uniroma3.siw.repository.TeamRepository;
import it.uniroma3.siw.repository.TournamentRepository;

@Service
public class TournamentService {
	
	private final TournamentRepository tournamentRepository;
	private final TeamRepository teamRepository;
	
	public TournamentService(TournamentRepository tournamentRepository, TeamRepository teamRepository) {
		this.tournamentRepository = tournamentRepository;
		this.teamRepository = teamRepository;
	}
	
	@Transactional(readOnly = true)
	public Tournament findById(Long id) {
		return this.tournamentRepository.findById(id).orElse(null);
	}
	
	@Transactional(readOnly = true)
	public List<Tournament> findAll() {
		return this.tournamentRepository.findAll();
	}
	
	@Transactional(readOnly = true)
	public List<Team> findTeamsByTournamentId(Long tournamentId) {
	    Tournament tournament = this.tournamentRepository.findByIdWithTeams(tournamentId)
	            .orElseThrow(() -> new IllegalArgumentException("Torneo non trovato: " + tournamentId));

	    return tournament.getTeams().stream().toList();
	}

	@Transactional
	public Tournament save(Tournament tournament) {
		return this.tournamentRepository.save(tournament);
	}
	
	@Transactional
	public Tournament update(Long id, Tournament formTournament) {
		Tournament tournament = this.findById(id);
		tournament.setName(formTournament.getName());
		tournament.setYear(formTournament.getYear());
		tournament.setDescription(formTournament.getDescription());
		return tournament;
	}
	
	@Transactional
	public void addTeamToTournament(Long tournamentId, Long teamId) {
		Tournament tournament = this.findById(tournamentId);
		Team team = this.teamRepository.findById(teamId).orElse(null);
		
		if (tournament != null && team != null && !tournament.getTeams().contains(team)) {
			tournament.getTeams().add(team);
		}
	}
	
	@Transactional(readOnly = true)
	public List<StandingRow> calculateStandings(Tournament tournament) {
	    Map<Long, StandingRow> standingMap = new HashMap<>();
	    
	    for (Team team : tournament.getTeams()) {
	        standingMap.put(team.getId(), new StandingRow(team));
	    }
	    
	    for (Match match : tournament.getMatches()) {
	        if (match.getStatus() == Match.Status.PLAYED) {
	            Integer gHome = match.getGoalsHome();
	            Integer gAway = match.getGoalsAway();

	            if (gHome == null || gAway == null) {
	                continue;
	            }

	            StandingRow homeRow = standingMap.get(match.getHomeTeam().getId());
	            StandingRow awayRow = standingMap.get(match.getAwayTeam().getId());

	            if (homeRow != null && awayRow != null) {
	                homeRow.updateStats(gHome, gAway);
	                awayRow.updateStats(gAway, gHome);
	            }
	        }
	    }
	    
	    List<StandingRow> standings = new ArrayList<>(standingMap.values());
	    standings.sort((a, b) -> {
	        if (b.getPoints() != a.getPoints()) {
	            return Integer.compare(b.getPoints(), a.getPoints());
	        }
	        return Integer.compare(b.getGoalDifference(), a.getGoalDifference());
	    });

	    return standings;
	}
	
	@Transactional
    public List<Tournament> searchTournaments(String name) {
        if (name == null || name.trim().isEmpty()) {
            return (List<Tournament>) tournamentRepository.findAll();
        }
        return tournamentRepository.findByNameContainingIgnoreCase(name);
    }
	
}
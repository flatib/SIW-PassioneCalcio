package it.uniroma3.siw.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.uniroma3.siw.model.Match;
import it.uniroma3.siw.model.Referee;
import it.uniroma3.siw.model.Team;
import it.uniroma3.siw.model.Tournament;
import it.uniroma3.siw.repository.MatchRepository;
import it.uniroma3.siw.repository.RefereeRepository;
import it.uniroma3.siw.repository.TeamRepository;
import it.uniroma3.siw.repository.TournamentRepository;

@Service
public class MatchService {

	private MatchRepository matchRepository;
	private TournamentRepository tournamentRepository;
	private TeamRepository teamRepository;
	private RefereeRepository refereeRepository;
	
	public MatchService(MatchRepository matchRepository, TournamentRepository tournamentRepository,
			TeamRepository teamRepository, RefereeRepository refereeRepository) {
		this.matchRepository = matchRepository;
		this.tournamentRepository = tournamentRepository;
		this.teamRepository = teamRepository;
		this.refereeRepository = refereeRepository;
	}
	
	@Transactional(readOnly = true)
	public List<Match> findAll() {
		return matchRepository.findAll();
	}
	
	@Transactional(readOnly = true)
	public Match findById(Long id) {
	    return this.matchRepository.findById(id)
	            .orElseThrow(() -> new IllegalArgumentException("Partita non trovata: " + id));
	}
	
	@Transactional(readOnly = true)
	public List<Match> findMatchesByTeamOrdered(Long teamId) {
		return matchRepository.findMatchesByTeamOrdered(teamId);
	}
	
	@Transactional(readOnly = true)
    public List<Match> findByTournament(Long tournamentId) {
        return this.matchRepository.findByTournamentId(tournamentId);
    }
	
	@Transactional
	public Match save(Match match, Long tournamentId, Long homeTeamId, Long awayTeamId, Long refereeId) {
	    Tournament tournament = this.tournamentRepository.findById(tournamentId)
	            .orElseThrow(() -> new IllegalArgumentException("Torneo non trovato"));

	    Team homeTeam = this.teamRepository.findById(homeTeamId)
	            .orElseThrow(() -> new IllegalArgumentException("Squadra di casa non trovata"));

	    Team awayTeam = this.teamRepository.findById(awayTeamId)
	            .orElseThrow(() -> new IllegalArgumentException("Squadra ospite non trovata"));

	    Referee referee = this.refereeRepository.findById(refereeId)
	            .orElseThrow(() -> new IllegalArgumentException("Arbitro non trovato"));

	    if (homeTeam.getId().equals(awayTeam.getId())) {
	        throw new IllegalArgumentException("Le due squadre devono essere diverse");
	    }

	    match.setTournament(tournament);
	    match.setHomeTeam(homeTeam);
	    match.setAwayTeam(awayTeam);
	    match.setReferee(referee);

	    if (match.getStatus() == null) {
	        match.setStatus(Match.Status.SCHEDULED);
	    }

	    return this.matchRepository.save(match);
	}
	
	@Transactional
	public Match updateResult(Long matchId, Integer goalsHome, Integer goalsAway) {
	    Match match = this.matchRepository.findById(matchId)
	            .orElseThrow(() -> new IllegalArgumentException("Partita non trovata"));

	    if (goalsHome == null || goalsAway == null || goalsHome < 0 || goalsAway < 0) {
	        throw new IllegalArgumentException("Il risultato inserito non è valido");
	    }

	    match.setGoalsHome(goalsHome);
	    match.setGoalsAway(goalsAway);
	    match.setStatus(Match.Status.PLAYED);

	    return this.matchRepository.save(match);
	}
	
	@Transactional
	public void deleteById(Long id) {
			    Match match = this.matchRepository.findById(id)
	            .orElseThrow(() -> new IllegalArgumentException("Partita non trovata"));
	    this.matchRepository.delete(match);
	}
	
	@Transactional(readOnly = true)
	public List<Match> searchMatchesByTournament(String tournamentName) {
	    if (tournamentName == null || tournamentName.trim().isEmpty()) {
	        return (List<Match>) matchRepository.findAll();
	    }
	    return matchRepository.findByTournamentNameContainingIgnoreCase(tournamentName);
	}
}

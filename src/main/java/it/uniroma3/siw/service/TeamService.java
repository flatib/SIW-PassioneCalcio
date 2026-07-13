package it.uniroma3.siw.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.uniroma3.siw.exception.DuplicateTeamException;
import it.uniroma3.siw.model.Team;
import it.uniroma3.siw.model.Tournament;
import it.uniroma3.siw.repository.TeamRepository;
import it.uniroma3.siw.repository.TournamentRepository;

@Service
public class TeamService {

	private TeamRepository teamRepository;
	private TournamentRepository tournamentRepository;
	
	public TeamService(TeamRepository teamRepository, TournamentRepository tournamentRepository) {
		this.teamRepository = teamRepository;
		this.tournamentRepository = tournamentRepository;
	}
	
	@Transactional(readOnly = true)
	public Team findById(Long id) {
		return this.teamRepository.findById(id).orElse(null);
	}
	
	@Transactional(readOnly = true)
	public List<Team> findAll() {
		return this.teamRepository.findAll();
	}
	
	@Transactional
	public Team save(Team team) {
		if (this.teamRepository.existsByNameIgnoreCase(team.getName())) {
			throw new DuplicateTeamException("Esiste già una squadra con questo nome");
		}
		return this.teamRepository.save(team);
	}
	
	@Transactional
	public Team update(Long id, Team formTeam) {
	    Team team = this.findById(id);
	    if (team == null) {
	        throw new IllegalArgumentException("Squadra non trovata");
	    }

	    boolean duplicate = this.teamRepository.existsByNameIgnoreCaseAndIdNot(formTeam.getName(), id);
	    if (duplicate) {
	    	throw new DuplicateTeamException("Esiste già un'altra squadra con questo nome");
	    }

	    team.setName(formTeam.getName());
	    team.setCity(formTeam.getCity());
	    team.setFoundationYear(formTeam.getFoundationYear());

	    return this.teamRepository.save(team);
	}
	
	@Transactional
	public void deleteById(Long id) {
		this.teamRepository.deleteById(id);
	}
	
	@Transactional
	public void addTeamToTournament(Long teamId, Long tournamentId) {
	    Team team = this.teamRepository.findById(teamId).orElseThrow(
	        () -> new IllegalArgumentException("Squadra non trovata")
	    );

	    Tournament tournament = this.tournamentRepository.findById(tournamentId).orElseThrow(
	        () -> new IllegalArgumentException("Torneo non trovato")
	    );

	    boolean alreadySubscribed = team.getTournaments().stream()
	            .anyMatch(t -> t.getId().equals(tournamentId));

	    if (alreadySubscribed) {
	        throw new IllegalArgumentException("La squadra è già iscritta a questo torneo");
	    }

	    team.getTournaments().add(tournament);
	    tournament.getTeams().add(team);

	    this.teamRepository.save(team);
	}
	
	@Transactional
	public void removeTeamFromTournament(Long teamId, Long tournamentId) {
	    Team team = this.teamRepository.findById(teamId)
	            .orElseThrow(() -> new IllegalArgumentException("Squadra non trovata"));

	    Tournament tournament = this.tournamentRepository.findById(tournamentId)
	            .orElseThrow(() -> new IllegalArgumentException("Torneo non trovato"));

	    boolean isSubscribed = team.getTournaments().stream()
	            .anyMatch(t -> t.getId().equals(tournamentId));

	    if (!isSubscribed) {
	        throw new IllegalArgumentException("La squadra non è iscritta a questo torneo");
	    }

	    team.getTournaments().removeIf(t -> t.getId().equals(tournamentId));
	    tournament.getTeams().removeIf(t -> t.getId().equals(teamId));

	    this.teamRepository.save(team);
	}
	
	@Transactional(readOnly = true)
	public List<Team> searchTeams(String name) {
	    if (name == null || name.trim().isEmpty()) {
	        return (List<Team>) teamRepository.findAll();
	    }
	    return teamRepository.findByNameContainingIgnoreCase(name);
	}
	
	// --- METODI PER ANALISI PRESTAZIONALE N+1 ---

	@Transactional(readOnly = true)
    public long unoptimizedFetch() {
        long start = System.currentTimeMillis();
        
        List<Team> teams = (List<Team>) teamRepository.findAll();
        for (Team team : teams) {
            int playersCount = team.getPlayers().size(); 
        }
        
        long end = System.currentTimeMillis();
        return end - start;
    }

    @Transactional(readOnly = true)
    public long optimizedFetch() {
        long start = System.currentTimeMillis();
        
        List<Team> teams = teamRepository.findAllWithPlayers();
        for (Team team : teams) {
            int playersCount = team.getPlayers().size();
        }
        
        long end = System.currentTimeMillis();
        return end - start;
    }
	
}
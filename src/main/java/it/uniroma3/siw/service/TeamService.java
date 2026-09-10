package it.uniroma3.siw.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import it.uniroma3.siw.exception.DuplicateTeamException;
import it.uniroma3.siw.model.Image;
import it.uniroma3.siw.model.Player;
import it.uniroma3.siw.model.Team;
import it.uniroma3.siw.model.Tournament;
import it.uniroma3.siw.repository.PlayerRepository;
import it.uniroma3.siw.repository.TeamRepository;
import it.uniroma3.siw.repository.TournamentRepository;

@Service
public class TeamService {

	private TeamRepository teamRepository;
	private TournamentRepository tournamentRepository;
	private PlayerRepository playerRepository;
	
	public TeamService(TeamRepository teamRepository, TournamentRepository tournamentRepository, PlayerRepository playerRepository) {
		this.teamRepository = teamRepository;
		this.tournamentRepository = tournamentRepository;
		this.playerRepository = playerRepository;
	}
	
	@Transactional(readOnly = true)
	public Team findById(Long id) {
		return this.teamRepository.findById(id).orElse(null);
	}
	
	@Transactional(readOnly = true)
	public List<Team> findAll() {
		return this.teamRepository.findAll();
	}
	
	@Transactional(readOnly = true)
	public Page<Team> searchTeamsPaginated(String search, Integer year, String city, int page, int size) {
	    return teamRepository.findFilteredTeamsPaginated(search, year, city, PageRequest.of(page, size));
	}

	@Transactional(readOnly = true)
	public List<String> getAvailableCities() {
	    return teamRepository.findDistinctCities();
	}

	@Transactional(readOnly = true)
	public List<Integer> getAvailableYears() {
	    return teamRepository.findDistinctYears();
	}
	
	@Transactional
	public Team save(Team team) {
		if (this.teamRepository.existsByNameIgnoreCase(team.getName())) {
			throw new DuplicateTeamException("Esiste già una squadra con questo nome");
		}
		return this.teamRepository.save(team);
	}
	
	@Transactional
	public Team update(Long id, Team formTeam, MultipartFile immagineFile) throws IOException {
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
	    
	    if (immagineFile != null && !immagineFile.isEmpty()) {
	        Image image = new Image();
	        image.setDati(immagineFile.getBytes());
	        image.setContentType(immagineFile.getContentType());
	        team.setImage(image);
	    }

	    return this.teamRepository.save(team);
	}
	
	@Transactional
    public void deleteById(Long id) {
        Team team = this.teamRepository.findById(id).orElse(null);
        if (team != null) {
            List<Tournament> tournaments = new ArrayList<>(team.getTournaments());
            for (Tournament tournament : tournaments) {
            	this.removeTeamFromTournament(id, tournament.getId());
            }
            List<Player> players = new ArrayList<>(team.getPlayers());
            for (Player player : players) {
				this.removePlayerFromTeam(player.getId(), id);
			}
            this.teamRepository.delete(team);
        }
    }
	
	@Transactional
	public void deleteAll(List<Long> ids) {
	    for (Long id : ids) {
	        this.deleteById(id);
	    }
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
	
	@Transactional
	public void addPlayerToTeam(Long teamId, Long playerId) {
		Team team = this.findById(teamId);
		Player player = this.playerRepository.findById(playerId).orElse(null);
		
		if (team != null && player != null && !team.getPlayers().contains(player)) {
			team.getPlayers().add(player);
			player.setTeam(team);
			this.teamRepository.save(team);
		}
	}
	
	@Transactional
	public void removePlayerFromTeam(Long playerId, Long teamId) {
	    Team team = this.teamRepository.findById(teamId)
	            .orElseThrow(() -> new IllegalArgumentException("Squadra non trovata"));

	    Player player = this.playerRepository.findById(playerId)
	            .orElseThrow(() -> new IllegalArgumentException("Giocatore non trovato"));

	    if (!team.getPlayers().contains(player)) {
	        throw new IllegalArgumentException("Il giocatore non appartiene a questa squadra");
	    }

	    team.getPlayers().remove(player);
	    player.setTeam(null);

	    this.teamRepository.save(team);
	    this.playerRepository.save(player);
	}
	
	@Transactional(readOnly = true)
	public List<Team> searchTeams(String name) {
	    if (name == null || name.trim().isEmpty()) {
	        return (List<Team>) teamRepository.findAll();
	    }
	    return teamRepository.findByNameContainingIgnoreCase(name);
	}
	
}
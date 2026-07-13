package it.uniroma3.siw.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.uniroma3.siw.exception.DuplicatePlayerException;
import it.uniroma3.siw.model.Player;
import it.uniroma3.siw.model.Team;
import it.uniroma3.siw.repository.PlayerRepository;
import it.uniroma3.siw.repository.TeamRepository;

@Service
public class PlayerService {

    private final PlayerRepository playerRepository;
    private final TeamRepository teamRepository;

    public PlayerService(PlayerRepository playerRepository, TeamRepository teamRepository) {
        this.playerRepository = playerRepository;
        this.teamRepository = teamRepository;
    }

    @Transactional(readOnly = true)
    public Player findById(Long id) {
        return this.playerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Giocatore non trovato: " + id));
    }

    @Transactional(readOnly = true)
    public List<Player> findByTeam(Long teamId) {
        return this.playerRepository.findByTeamId(teamId);
    }

    @Transactional(readOnly = true)
    public List<Player> findAll() {
        return this.playerRepository.findAll();
    }

    @Transactional
    public Player save(Player player, Long teamId) {
        Team team = this.teamRepository.findById(teamId)
                .orElseThrow(() -> new IllegalArgumentException("Squadra non trovata: " + teamId));

        normalize(player);
        validateDuplicate(player.getName(), player.getSurname(), null);

        player.setTeam(team);
        return this.playerRepository.save(player);
    }

    @Transactional
    public Player update(Long id, Player formPlayer, Long teamId) {
        Player player = this.playerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Giocatore non trovato: " + id));

        Team team = this.teamRepository.findById(teamId)
                .orElseThrow(() -> new IllegalArgumentException("Squadra non trovata: " + teamId));

        normalize(formPlayer);
        validateDuplicate(formPlayer.getName(), formPlayer.getSurname(), id);

        player.setName(formPlayer.getName());
        player.setSurname(formPlayer.getSurname());
        player.setDateOfBirth(formPlayer.getDateOfBirth());
        player.setRole(formPlayer.getRole());
        player.setHeight(formPlayer.getHeight());
        player.setTeam(team);

        return this.playerRepository.save(player);
    }

    private void normalize(Player player) {
        if (player.getName() != null) {
            player.setName(player.getName().trim());
        }
        if (player.getSurname() != null) {
            player.setSurname(player.getSurname().trim());
        }
    }

    private void validateDuplicate(String name, String surname, Long currentPlayerId) {
        if (name == null || name.isBlank() || surname == null || surname.isBlank()) {
            return;
        }

        boolean duplicateExists;

        if (currentPlayerId == null) {
            duplicateExists = this.playerRepository
                    .existsByNameIgnoreCaseAndSurnameIgnoreCase(name, surname);
        } else {
            duplicateExists = this.playerRepository
                    .existsByNameIgnoreCaseAndSurnameIgnoreCaseAndIdNot(name, surname, currentPlayerId);
        }

        if (duplicateExists) {
            throw new DuplicatePlayerException(name, surname);
        }
    }
    
    @Transactional
    public void deleteById(Long id) {
		this.playerRepository.deleteById(id);
	}
    
    @Transactional(readOnly = true)
    public List<Player> searchPlayers(String query) {
        if (query == null || query.trim().isEmpty()) {
            return (List<Player>) playerRepository.findAll();
        }
        return playerRepository.findByNameContainingIgnoreCaseOrSurnameContainingIgnoreCase(query, query);
    }
}
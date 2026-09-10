package it.uniroma3.siw;

import it.uniroma3.siw.model.Player;
import it.uniroma3.siw.model.Team;
import it.uniroma3.siw.repository.PlayerRepository;
import it.uniroma3.siw.repository.TeamRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class TeamPerformanceIntegrationTest {
	
	

    @Autowired
    private TeamRepository teamRepository;
    
    @Autowired
    private PlayerRepository playerRepository;

    @BeforeEach
    void setUpDatabase() {
        if (teamRepository.count() >= 50) return;
        for (int i = 1; i <= 50; i++) {
            Team team = new Team();
            team.setName("Squadra Test " + i);
            team.setCity("Città");
            team.setFoundationYear(1990);
            team.setPlayers(new ArrayList<>()); 
            team = teamRepository.save(team);
            List<Player> players = new ArrayList<>();
            for (int j = 1; j <= 4; j++) {
                Player player = new Player();
                player.setName("Nome" + j);
                player.setSurname("Giocatore" + i + "-" + j);
                player.setDateOfBirth(LocalDate.of(2000, 1, 1));
                player.setRole("Attaccante");
                player.setHeight(180);
                player.setTeam(team);
                team.getPlayers().add(player);
                players.add(player);
            }
            playerRepository.saveAll(players);
        }
    }

    @Test
    @Transactional
    void testUnoptimizedFetchNPlusOne() {
        long start = System.currentTimeMillis();
        // Comportamento N+1: 1 query per le squadre, N query per i giocatori
        List<Team> teams = (List<Team>) teamRepository.findAll();
        for (Team team : teams) {
            int playersCount = team.getPlayers().size();
        }
        long end = System.currentTimeMillis();
        System.out.println("=== ANALISI PRESTAZIONALE ===");
        System.out.println("Strategia N+1 (Lazy) completata in: " + (end - start) + " ms su " + teams.size() + " squadre.");
    }

    @Test
    @Transactional
    void testOptimizedFetchJoin() {
        long start = System.currentTimeMillis();
        // Comportamento Ottimizzato: 1 singola query con join fetch
        List<Team> teams = teamRepository.findAllWithPlayers();
        for (Team team : teams) {
            int playersCount = team.getPlayers().size();
        }
        long end = System.currentTimeMillis();
        System.out.println("=== ANALISI PRESTAZIONALE ===");
        System.out.println("Strategia Ottimizzata (Join Fetch) completata in: " + (end - start) + " ms su " + teams.size() + " squadre.");
    }
}
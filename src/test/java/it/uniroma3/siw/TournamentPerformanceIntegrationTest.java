package it.uniroma3.siw;

import it.uniroma3.siw.model.Team;
import it.uniroma3.siw.model.Tournament;
import it.uniroma3.siw.repository.TeamRepository;
import it.uniroma3.siw.repository.TournamentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@SpringBootTest
class TournamentPerformanceIntegrationTest {

    @Autowired
    private TournamentRepository tournamentRepository;

    @Autowired
    private TeamRepository teamRepository;

    @BeforeEach
    void setUpDatabase() {
        if (tournamentRepository.count() > 0) return;
        System.out.println("=== INIZIALIZZAZIONE DATI TEST TORNEI ===");
        Tournament tournament = new Tournament();
        tournament.setName("Torneo Prestazionale N+1");
        tournament.setYear(2026);
        tournament.setDescription("Torneo per testare il Join Fetch");
        tournament.setTeams(new ArrayList<>());
        tournament = tournamentRepository.save(tournament);
        for (int i = 1; i <= 30; i++) {
            Team team = new Team();
            team.setName("Squadra Test Torneo " + i);
            team.setCity("Roma");
            team.setFoundationYear(2000);
            team = teamRepository.save(team);
            
            tournament.getTeams().add(team);
        }
        tournamentRepository.save(tournament);
    }

    @Test
    @Transactional
    void testLazyLoadingSingoloTorneo() {
        Long tournamentId = tournamentRepository.findAll().get(0).getId();
        System.out.println("=== TEST LAZY LOADING (TORNEO E SQUADRE) ===");
        long start = System.currentTimeMillis();
        // 1° query: Carica solo dati base torneo (nome, anno, descrizione)
        // collezione teams essendo @ManyToMany è lazy di default
        Tournament tournament = tournamentRepository.findById(tournamentId).orElse(null);
        if (tournament != null) {
            // 2° Query: Il proxy va a leggere la tabella di join tournament_teams
            int numTeams = tournament.getTeams().size();
            long end = System.currentTimeMillis();
            System.out.println("Strategia LAZY (2 Query): " + (end - start) + " ms per caricare " + numTeams + " squadre.");
        }
    }

    @Test
    @Transactional
    void testJoinFetchSingoloTorneo() {
        Long tournamentId = tournamentRepository.findAll().get(0).getId();
        System.out.println("=== TEST JOIN FETCH (TORNEO E SQUADRE) ===");
        long start = System.currentTimeMillis();
        // 1 sola query: left join fetch carica simultaneamente il torneo e tutte le sue squadre
        Tournament tournament = tournamentRepository.findByIdWithTeams(tournamentId).orElse(null);
        if (tournament != null) {
            // no query aggiuntive: squadre già presenti in ram
            int numTeams = tournament.getTeams().size();
            long end = System.currentTimeMillis();
            System.out.println("Strategia OTTMIZZATA (1 Query): " + (end - start) + " ms per caricare " + numTeams + " squadre.");
        }
    }
}
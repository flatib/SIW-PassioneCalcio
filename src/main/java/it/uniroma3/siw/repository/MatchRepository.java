package it.uniroma3.siw.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import it.uniroma3.siw.model.Match;

public interface MatchRepository extends CrudRepository<Match, Long> {

	@Query("SELECT m FROM Match m ORDER BY m.dateAndTime ASC, m.tournament.name ASC")
	List<Match> findAll();

	@Query("""
			SELECT m
			FROM Match m
			WHERE m.homeTeam.id = :teamId OR m.awayTeam.id = :teamId
			ORDER BY m.dateAndTime ASC
			""")
	List<Match> findMatchesByTeamOrdered(@Param("teamId") Long teamId);

	@Query("""
			SELECT m
			FROM Match m
			WHERE m.tournament.id = :tournamentId
			ORDER BY m.dateAndTime ASC
			""")
	List<Match> findByTournamentId(@Param("tournamentId") Long tournamentId);
	
	List<Match> findByTournamentNameContainingIgnoreCase(String tournamentName);
}
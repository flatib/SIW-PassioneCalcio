package it.uniroma3.siw.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
	
	Page<Match> findAllByOrderByDateAndTimeDesc(Pageable pageable);
	
	@Query("SELECT m FROM Match m " +
		       "LEFT JOIN FETCH m.tournament " +
		       "LEFT JOIN FETCH m.homeTeam " +
		       "LEFT JOIN FETCH m.awayTeam " +
		       "LEFT JOIN FETCH m.referee " +
		       "WHERE (:search IS NULL OR :search = '' OR LOWER(m.tournament.name) LIKE LOWER(CONCAT('%', :search, '%')) " +
		       "OR LOWER(m.homeTeam.name) LIKE LOWER(CONCAT('%', :search, '%')) " +
		       "OR LOWER(m.awayTeam.name) LIKE LOWER(CONCAT('%', :search, '%'))) " +
		       "AND (cast(:startDate as date) IS NULL OR m.dateAndTime >= :startDate) " +
		       "AND (cast(:endDate as date) IS NULL OR m.dateAndTime <= :endDate) " +
		       "ORDER BY m.dateAndTime DESC")
		Page<Match> findFilteredMatchesPaginated(@Param("search") String search, 
		                                         @Param("startDate") LocalDateTime startDate, 
		                                         @Param("endDate") LocalDateTime endDate, 
		                                         Pageable pageable);
}
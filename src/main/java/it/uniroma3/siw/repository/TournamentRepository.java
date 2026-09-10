package it.uniroma3.siw.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import it.uniroma3.siw.model.Tournament;

public interface TournamentRepository extends CrudRepository<Tournament, Long> {

	List<Tournament> findAll();

	Optional<Tournament> findByNameAndYear(String name, Integer year);
	
	@Query("""
		    select t
		    from Tournament t
		    left join fetch t.teams
		    where t.id = :id
		""")
		Optional<Tournament> findByIdWithTeams(@Param("id") Long id);

	boolean existsByNameIgnoreCaseAndYear(String name, Integer year);
	
	List<Tournament> findByNameContainingIgnoreCase(String name);
	
	@Query("SELECT t FROM Tournament t LEFT JOIN FETCH t.image WHERE "
	        + "(:search IS NULL OR :search = '' OR LOWER(t.name) LIKE LOWER(CONCAT('%', :search, '%'))) "
	        + "AND (:year IS NULL OR t.year = :year) "
	        + "ORDER BY t.name ASC")
	Page<Tournament> findFilteredTournamentsPaginated(@Param("search") String search,
	                                                   @Param("year") Integer year,
	                                                   Pageable pageable);
	
	@Query("SELECT DISTINCT t.year FROM Tournament t WHERE t.year IS NOT NULL ORDER BY t.year DESC")
	List<Integer> findDistinctYears();
}
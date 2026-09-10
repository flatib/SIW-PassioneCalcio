package it.uniroma3.siw.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import it.uniroma3.siw.model.Team;

public interface TeamRepository extends CrudRepository<Team, Long> {

	List<Team> findAll();

	Optional<Team> findByName(String name);

	boolean existsByName(String name);

	boolean existsByNameIgnoreCase(String name);

	boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);
	
	List<Team> findByNameContainingIgnoreCase(String name);
	
	@Query("SELECT DISTINCT t FROM Team t LEFT JOIN FETCH t.players")
    List<Team> findAllWithPlayers();
	
	@Query("SELECT t FROM Team t LEFT JOIN FETCH t.image WHERE " +
		       "(:search IS NULL OR :search = '' OR LOWER(t.name) LIKE LOWER(CONCAT('%', :search, '%'))) " +
		       "AND (:year IS NULL OR t.foundationYear = :year) " +
		       "AND (:city IS NULL OR :city = '' OR t.city = :city) " +
		       "ORDER BY t.name ASC")
	    Page<Team> findFilteredTeamsPaginated(@Param("search") String search,
	                                          @Param("year") Integer year,
	                                          @Param("city") String city,
	                                          Pageable pageable);

	    @Query("SELECT DISTINCT t.city FROM Team t WHERE t.city IS NOT NULL ORDER BY t.city ASC")
	    List<String> findDistinctCities();

	    @Query("SELECT DISTINCT t.foundationYear FROM Team t WHERE t.foundationYear IS NOT NULL ORDER BY t.foundationYear DESC")
	    List<Integer> findDistinctYears();
}
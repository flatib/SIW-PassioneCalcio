package it.uniroma3.siw.repository;

import java.util.List;
import java.util.Optional;

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

	boolean existsByNameAndYear(String name, Integer year);
	
	List<Tournament> findByNameContainingIgnoreCase(String name);
}
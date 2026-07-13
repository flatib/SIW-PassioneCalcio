package it.uniroma3.siw.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

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
	
	
}
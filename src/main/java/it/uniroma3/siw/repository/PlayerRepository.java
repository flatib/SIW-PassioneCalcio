package it.uniroma3.siw.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import it.uniroma3.siw.model.Player;

public interface PlayerRepository extends CrudRepository<Player, Long> {

    @Query("SELECT p FROM Player p ORDER BY p.surname ASC, p.name ASC")
    List<Player> findAll();

    @Query("SELECT p FROM Player p WHERE p.team.id = :teamId ORDER BY p.surname ASC, p.name ASC")
    List<Player> findByTeamId(Long teamId);

    boolean existsByNameIgnoreCaseAndSurnameIgnoreCase(String name, String surname);

    boolean existsByNameIgnoreCaseAndSurnameIgnoreCaseAndIdNot(String name, String surname, Long id);
    
    List<Player> findByNameContainingIgnoreCaseOrSurnameContainingIgnoreCase(String name, String surname);
}
package it.uniroma3.siw.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import it.uniroma3.siw.model.Player;

public interface PlayerRepository extends CrudRepository<Player, Long> {

    @Query("SELECT p FROM Player p ORDER BY p.surname ASC, p.name ASC")
    List<Player> findAll();

    @Query("SELECT p FROM Player p WHERE p.team.id = :teamId ORDER BY p.surname ASC, p.name ASC")
    List<Player> findByTeamId(Long teamId);

    boolean existsByNameIgnoreCaseAndSurnameIgnoreCase(String name, String surname);

    boolean existsByNameIgnoreCaseAndSurnameIgnoreCaseAndIdNot(String name, String surname, Long id);
    
    List<Player> findByNameContainingIgnoreCaseOrSurnameContainingIgnoreCase(String name, String surname);
    
    Page<Player> findAllByOrderBySurnameAscNameAsc(Pageable pageable);
    
    @Query("SELECT p FROM Player p LEFT JOIN FETCH p.team t WHERE " +
    	       "(:search IS NULL OR :search = '' OR LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')) " +
    	       "OR LOWER(p.surname) LIKE LOWER(CONCAT('%', :search, '%')) " +
    	       "OR LOWER(t.name) LIKE LOWER(CONCAT('%', :search, '%'))) " +
    	       "AND (:role IS NULL OR :role = '' OR p.role = :role) " +
    	       "AND (:onlyFreeAgents = false OR p.team IS NULL) " +
    	       "ORDER BY p.surname ASC, p.name ASC")
     Page<Player> findFilteredPlayersPaginated(@Param("search") String search,
                                               @Param("role") String role,
                                               @Param("onlyFreeAgents") boolean onlyFreeAgents,
                                               Pageable pageable);
}
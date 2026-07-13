package it.uniroma3.siw.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import it.uniroma3.siw.model.Referee;

public interface RefereeRepository extends CrudRepository<Referee, Long> {

	List<Referee> findAll();
}
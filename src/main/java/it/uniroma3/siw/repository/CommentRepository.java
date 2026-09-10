package it.uniroma3.siw.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import it.uniroma3.siw.model.Comment;

public interface CommentRepository extends CrudRepository<Comment, Long> {

	List<Comment> findByMatchIdOrderByCreatedAtAsc(Long matchId);

	Optional<Comment> findByMatchIdAndAuthorId(Long matchId, Long authorId);

	List<Comment> findAll();
}
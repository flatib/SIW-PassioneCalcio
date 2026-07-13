package it.uniroma3.siw.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.uniroma3.siw.model.Comment;
import it.uniroma3.siw.model.Match;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.repository.CommentRepository;
import it.uniroma3.siw.repository.MatchRepository;
import it.uniroma3.siw.repository.UserRepository;

@Service
public class CommentService {

	private final CommentRepository commentRepository;
	private final UserRepository userRepository;
	private final MatchRepository matchRepository;

	public CommentService(CommentRepository commentRepository,
						  UserRepository userRepository,
						  MatchRepository matchRepository) {
		this.commentRepository = commentRepository;
		this.userRepository = userRepository;
		this.matchRepository = matchRepository;
	}

	@Transactional(readOnly = true)
	public Comment findById(Long id) {
		return this.commentRepository.findById(id).orElse(null);
	}

	@Transactional(readOnly = true)
	public List<Comment> findCommentsByMatch(Long matchId) {
		return this.commentRepository.findByMatchIdOrderByCreatedAtAsc(matchId);
	}

	@Transactional(readOnly = true)
	public Comment findCommentByMatchAndAuthor(Long matchId, Long authorId) {
		return this.commentRepository.findByMatchIdAndAuthorId(matchId, authorId).orElse(null);
	}

	@Transactional
	public Comment save(Comment comment, Long matchId, Long authorId) {
		Match match = this.matchRepository.findById(matchId).orElse(null);
		User author = this.userRepository.findById(authorId).orElse(null);

		comment.setMatch(match);
		comment.setAuthor(author);

		return this.commentRepository.save(comment);
	}

	@Transactional
	public Comment saveOrUpdateComment(String text, Long matchId, Long authorId) {
		Comment existing = this.commentRepository
				.findByMatchIdAndAuthorId(matchId, authorId)
				.orElse(null);

		if (existing != null) {
			existing.setText(text);
			return existing;
		}

		Match match = this.matchRepository.findById(matchId).orElse(null);
		User author = this.userRepository.findById(authorId).orElse(null);

		Comment comment = new Comment();
		comment.setText(text);
		comment.setMatch(match);
		comment.setAuthor(author);

		return this.commentRepository.save(comment);
	}

	@Transactional
	public Comment updateByUsername(Long commentId, String newText, String username) {
	    Comment comment = this.commentRepository.findById(commentId).orElse(null);

	    if (comment == null) {
	        throw new RuntimeException("Commento non trovato");
	    }

	    if (!comment.getAuthor().getUsername().equals(username)) {
	        throw new RuntimeException("Non puoi modificare il commento di un altro utente");
	    }

	    comment.setText(newText);
	    return comment;
	}
}
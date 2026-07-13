package it.uniroma3.siw.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import it.uniroma3.siw.model.Match;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.service.CommentService;
import it.uniroma3.siw.service.MatchService;
import it.uniroma3.siw.service.UserService;

@Controller
@RequestMapping("/matches")
public class MatchController {
	
	private final MatchService matchService;
	private final UserService userService;
	private final CommentService commentService;
	
	public MatchController(MatchService matchService, UserService userService, CommentService commentService) {
		this.matchService = matchService;
		this.userService = userService;
		this.commentService = commentService;
	}
	
	@GetMapping
	public String list(Model model) {
		Map<LocalDate, List<Match>> matchesByDate = this.matchService.findAll().stream()
		        .collect(Collectors.groupingBy(
		            match -> match.getDateAndTime().toLocalDate(),
		            TreeMap::new,
		            Collectors.toList()
		        ));
	    model.addAttribute("matchesByDate", matchesByDate);
	    return "matches/list";
	}
	
	@GetMapping("/{id}")
	public String show(@PathVariable("id") Long id, Model model) {
	    Match match = this.matchService.findById(id);

	    if (match == null) {
	    	return "redirect:/tournaments";
	    }

	    model.addAttribute("match", match);
	    model.addAttribute("comments", this.commentService.findCommentsByMatch(id));
	    return "matches/detail";
	}
	
	@PostMapping("/{id}/comments")
	public String addComment(@PathVariable("id") Long matchId,
							 @RequestParam("text") String text,
							 Model model) {
		Match match = this.matchService.findById(matchId);

		if (match == null) {
			return "redirect:/tournaments";
		}

		if (text == null || text.trim().isEmpty()) {
			model.addAttribute("match", match);
			model.addAttribute("comments", this.commentService.findCommentsByMatch(matchId));
			model.addAttribute("commentError", "Il testo del commento è obbligatorio");
			return "matches/detail";
		}

		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		User currentUser = this.userService.findByUsername(username);

		if (currentUser != null) {
			this.commentService.saveOrUpdateComment(text, matchId, currentUser.getId());
		}

		return "redirect:/matches/" + matchId;
	}
}
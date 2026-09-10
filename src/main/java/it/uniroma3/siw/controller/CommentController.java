package it.uniroma3.siw.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import it.uniroma3.siw.model.Comment;
import it.uniroma3.siw.service.CommentService;

@Controller
@RequestMapping("/comments")
public class CommentController {

	private final CommentService commentService;

	public CommentController(CommentService commentService) {
		this.commentService = commentService;
	}

	@GetMapping("/{id}/edit")
	public String editCommentForm(@PathVariable("id") Long id, Model model) {
		Comment comment = this.commentService.findById(id);

		if (comment == null) {
			return "redirect:/tournaments";
		}

		String username = SecurityContextHolder.getContext().getAuthentication().getName();

		if (!comment.getAuthor().getUsername().equals(username)) {
			return "redirect:/matches/" + comment.getMatch().getId();
		}

		model.addAttribute("comment", comment);
		return "comments/edit";
	}

	@PostMapping("/{id}/edit")
	public String editComment(@PathVariable("id") Long id,
	                          @RequestParam("text") String text,
	                          Model model) {
	    Comment comment = this.commentService.findById(id);

	    if (comment == null) {
	        return "redirect:/tournaments";
	    }

	    String username = SecurityContextHolder.getContext().getAuthentication().getName();

	    if (text == null || text.trim().isEmpty()) {
	        model.addAttribute("comment", comment);
	        model.addAttribute("errorMessage", "Il testo del commento è obbligatorio");
	        return "comments/edit";
	    }

	    try {
	        this.commentService.updateByUsername(id, text, username);
	        return "redirect:/matches/" + comment.getMatch().getId();
	    } catch (RuntimeException e) {
	        model.addAttribute("comment", comment);
	        model.addAttribute("errorMessage", e.getMessage());
	        return "comments/edit";
	    }
	}
}
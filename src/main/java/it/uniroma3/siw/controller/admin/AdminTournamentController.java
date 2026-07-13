package it.uniroma3.siw.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import it.uniroma3.siw.model.Tournament;
import it.uniroma3.siw.service.TournamentService;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin/tournaments")
public class AdminTournamentController {

    private final TournamentService tournamentService;

    public AdminTournamentController(TournamentService tournamentService) {
        this.tournamentService = tournamentService;
    }

    @GetMapping
    public String index(Model model) {
        model.addAttribute("tournaments", this.tournamentService.findAll());
        return "admin/tournaments/index";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("tournament", new Tournament());
        return "admin/tournaments/form";
    }

    @PostMapping
    public String createTournament(@Valid @ModelAttribute("tournament") Tournament tournament,
                                   BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "admin/tournaments/form";
        }

        this.tournamentService.save(tournament);
        return "redirect:/tournaments";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Tournament tournament = this.tournamentService.findById(id);
        if (tournament == null) {
            return "redirect:/admin/tournaments";
        }

        model.addAttribute("tournament", tournament);
        return "admin/tournaments/form";
    }

    @PostMapping("/{id}")
    public String updateTournament(@PathVariable Long id,
                                   @Valid @ModelAttribute("tournament") Tournament tournament,
                                   BindingResult bindingResult,
                                   Model model) {
        if (bindingResult.hasErrors()) {
            return "admin/tournaments/form";
        }

        try {
            this.tournamentService.update(id, tournament);
            return "redirect:/admin/tournaments";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "admin/tournaments/form";
        }
    }
}
package it.uniroma3.siw.controller.admin;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import it.uniroma3.siw.model.Match;
import it.uniroma3.siw.service.MatchService;
import it.uniroma3.siw.service.RefereeService;
import it.uniroma3.siw.service.TeamService;
import it.uniroma3.siw.service.TournamentService;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin/matches")
public class AdminMatchController {

    private final MatchService matchService;
    private final TournamentService tournamentService;
    private final RefereeService refereeService;

    public AdminMatchController(MatchService matchService,
                                TournamentService tournamentService,
                                TeamService teamService,
                                RefereeService refereeService) {
        this.matchService = matchService;
        this.tournamentService = tournamentService;
        this.refereeService = refereeService;
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("match", new Match());
        model.addAttribute("tournaments", this.tournamentService.findAll());
        model.addAttribute("teams", List.of());
        model.addAttribute("referees", this.refereeService.findAll());
        return "admin/matches/form";
    }

    @PostMapping
    public String save(@Valid @ModelAttribute("match") Match match,
                       BindingResult bindingResult,
                       @RequestParam("tournamentId") Long tournamentId,
                       @RequestParam("homeTeamId") Long homeTeamId,
                       @RequestParam("awayTeamId") Long awayTeamId,
                       @RequestParam("refereeId") Long refereeId,
                       Model model) {

        populateFormModel(model, tournamentId);

        model.addAttribute("selectedTournamentId", tournamentId);
        model.addAttribute("selectedHomeTeamId", homeTeamId);
        model.addAttribute("selectedAwayTeamId", awayTeamId);
        model.addAttribute("selectedRefereeId", refereeId);

        if (bindingResult.hasErrors()) {
            return "admin/matches/form";
        }

        try {
            Match savedMatch = this.matchService.save(match, tournamentId, homeTeamId, awayTeamId, refereeId);
            return "redirect:/matches/" + savedMatch.getId();
        } catch (IllegalArgumentException e) {
            bindingResult.reject("match.invalid", e.getMessage());
            return "admin/matches/form";
        }
    }

    private void populateFormModel(Model model, Long tournamentId) {
        model.addAttribute("tournaments", this.tournamentService.findAll());
        model.addAttribute("referees", this.refereeService.findAll());

        if (tournamentId != null) {
            model.addAttribute("teams", this.tournamentService.findTeamsByTournamentId(tournamentId));
        } else {
            model.addAttribute("teams", List.of());
        }
    }

    @GetMapping("/{id}/result")
    public String resultForm(@PathVariable("id") Long id, Model model) {
        Match match = this.matchService.findById(id);

        if (match == null) {
            throw new IllegalArgumentException("Partita non trovata");
        }

        model.addAttribute("match", match);
        return "admin/matches/result-form";
    }

    @PostMapping("/{id}/result")
    public String updateResult(@PathVariable("id") Long id,
                               @RequestParam("goalsHome") Integer goalsHome,
                               @RequestParam("goalsAway") Integer goalsAway,
                               Model model) {

        Match match = this.matchService.findById(id);

        if (match == null) {
            throw new IllegalArgumentException("Partita non trovata");
        }

        model.addAttribute("match", match);

        if (goalsHome == null || goalsAway == null) {
            model.addAttribute("resultError", "Entrambi i punteggi sono obbligatori.");
            return "admin/matches/result-form";
        }

        if (goalsHome < 0 || goalsAway < 0) {
            model.addAttribute("resultError", "I gol non possono essere negativi.");
            return "admin/matches/result-form";
        }

        try {
            this.matchService.updateResult(id, goalsHome, goalsAway);
            return "redirect:/matches/" + id;
        } catch (IllegalArgumentException e) {
            model.addAttribute("resultError", e.getMessage());
            return "admin/matches/result-form";
        }
    }
    
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable("id") Long id) {
        this.matchService.deleteById(id);
        return "redirect:/matches";
    }
}
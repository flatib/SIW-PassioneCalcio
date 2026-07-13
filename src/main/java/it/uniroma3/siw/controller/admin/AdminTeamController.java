package it.uniroma3.siw.controller.admin;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import it.uniroma3.siw.exception.DuplicateTeamException;
import it.uniroma3.siw.model.Team;
import it.uniroma3.siw.model.Tournament;
import it.uniroma3.siw.service.TeamService;
import it.uniroma3.siw.service.TournamentService;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin/teams")
public class AdminTeamController {

    private final TeamService teamService;
    private final TournamentService tournamentService;

    public AdminTeamController(TeamService teamService, TournamentService tournamentService) {
        this.teamService = teamService;
        this.tournamentService = tournamentService;
    }

    @GetMapping
    public String index(Model model) {
        model.addAttribute("teams", this.teamService.findAll());
        return "admin/teams/index";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("team", new Team());
        model.addAttribute("formMode", "create");
        return "admin/teams/form";
    }

    @PostMapping
    public String save(@Valid @ModelAttribute("team") Team team,
                       BindingResult bindingResult,
                       Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("formMode", "create");
            return "admin/teams/form";
        }

        try {
            this.teamService.save(team);
            return "redirect:/teams";
        } catch (DuplicateTeamException e) {
            bindingResult.reject("team.duplicate", e.getMessage());
            model.addAttribute("formMode", "create");
            return "admin/teams/form";
        }
    }

    @GetMapping("/{id}/manage")
    public String manageTeam(@PathVariable("id") Long id, Model model) {
        Team team = this.teamService.findById(id);
        populateManageModel(model, team);
        return "admin/teams/manage";
    }

    @PostMapping("/{id}/tournaments")
    public String addTournament(@PathVariable("id") Long teamId,
                                @RequestParam("tournamentId") Long tournamentId,
                                Model model) {
        try {
            this.teamService.addTeamToTournament(teamId, tournamentId);
            return "redirect:/admin/teams/" + teamId + "/manage";
        } catch (IllegalArgumentException e) {
            Team team = this.teamService.findById(teamId);
            populateManageModel(model, team);
            model.addAttribute("errorMessage", e.getMessage());
            return "admin/teams/manage";
        }
    }

    @PostMapping("/{id}/tournaments/{tournamentId}/remove")
    public String removeTournament(@PathVariable("id") Long teamId,
                                   @PathVariable("tournamentId") Long tournamentId,
                                   Model model) {
        try {
            this.teamService.removeTeamFromTournament(teamId, tournamentId);
            return "redirect:/admin/teams/" + teamId + "/manage";
        } catch (IllegalArgumentException e) {
            Team team = this.teamService.findById(teamId);
            populateManageModel(model, team);
            model.addAttribute("errorMessage", e.getMessage());
            return "admin/teams/manage";
        }
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable("id") Long id, Model model) {
        Team team = this.teamService.findById(id);
        model.addAttribute("team", team);
        model.addAttribute("formMode", "edit");
        return "admin/teams/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable("id") Long id,
                         @Valid @ModelAttribute("team") Team team,
                         BindingResult bindingResult,
                         Model model) {
        if (bindingResult.hasErrors()) {
            team.setId(id);
            model.addAttribute("formMode", "edit");
            return "admin/teams/form";
        }

        try {
            this.teamService.update(id, team);
            return "redirect:/admin/teams/" + id + "/manage";
        } catch (DuplicateTeamException e) {
            team.setId(id);
            bindingResult.reject("team.duplicate", e.getMessage());
            model.addAttribute("formMode", "edit");
            return "admin/teams/form";
        }
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable("id") Long id) {
        this.teamService.deleteById(id);
        return "redirect:/teams";
    }

    private void populateManageModel(Model model, Team team) {
        Set<Long> currentTournamentIds = team.getTournaments().stream()
                .map(Tournament::getId)
                .collect(Collectors.toSet());

        List<Tournament> availableTournaments = this.tournamentService.findAll()
                .stream()
                .filter(tournament -> !currentTournamentIds.contains(tournament.getId()))
                .toList();

        model.addAttribute("team", team);
        model.addAttribute("availableTournaments", availableTournaments);
    }
}
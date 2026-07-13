package it.uniroma3.siw.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import it.uniroma3.siw.exception.DuplicatePlayerException;
import it.uniroma3.siw.model.Player;
import it.uniroma3.siw.service.PlayerService;
import it.uniroma3.siw.service.TeamService;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin/players")
public class AdminPlayerController {

    private final PlayerService playerService;
    private final TeamService teamService;

    public AdminPlayerController(PlayerService playerService, TeamService teamService) {
        this.playerService = playerService;
        this.teamService = teamService;
    }

    @GetMapping("/new")
    public String createForm(@RequestParam(required = false) Long teamId, Model model) {
        model.addAttribute("player", new Player());
        model.addAttribute("teams", this.teamService.findAll());
        model.addAttribute("selectedTeamId", teamId);
        return "admin/players/form";
    }

    @PostMapping
    public String save(@Valid @ModelAttribute("player") Player player,
                       BindingResult bindingResult,
                       @RequestParam("teamId") Long teamId,
                       Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("teams", this.teamService.findAll());
            model.addAttribute("selectedTeamId", teamId);
            return "admin/players/form";
        }

        try {
            this.playerService.save(player, teamId);
            return "redirect:/admin/teams/" + teamId + "/manage";
        } catch (DuplicatePlayerException e) {
            bindingResult.reject("player.duplicate");
            model.addAttribute("teams", this.teamService.findAll());
            model.addAttribute("selectedTeamId", teamId);
            return "admin/players/form";
        }
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable("id") Long id, Model model) {
        Player player = this.playerService.findById(id);

        model.addAttribute("player", player);
        model.addAttribute("teams", this.teamService.findAll());
        model.addAttribute("selectedTeamId", player.getTeam().getId());
        return "admin/players/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable("id") Long id,
                         @Valid @ModelAttribute("player") Player player,
                         BindingResult bindingResult,
                         @RequestParam("teamId") Long teamId,
                         Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("teams", this.teamService.findAll());
            model.addAttribute("selectedTeamId", teamId);
            return "admin/players/form";
        }

        try {
            this.playerService.update(id, player, teamId);
            return "redirect:/admin/teams/" + teamId + "/manage";
        } catch (DuplicatePlayerException e) {
            bindingResult.reject("player.duplicate");
            model.addAttribute("teams", this.teamService.findAll());
            model.addAttribute("selectedTeamId", teamId);
            return "admin/players/form";
        }
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable("id") Long id) {
        Player player = this.playerService.findById(id);
        Long teamId = player.getTeam().getId();

        this.playerService.deleteById(id);

        return "redirect:/admin/teams/" + teamId + "/manage";
    }
}
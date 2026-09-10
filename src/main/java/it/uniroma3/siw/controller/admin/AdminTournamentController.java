package it.uniroma3.siw.controller.admin;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import it.uniroma3.siw.exception.DuplicateTournamentException;
import it.uniroma3.siw.model.Image;
import it.uniroma3.siw.model.Team;
import it.uniroma3.siw.model.Tournament;
import it.uniroma3.siw.service.TeamService;
import it.uniroma3.siw.service.TournamentService;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin/tournaments")
public class AdminTournamentController {

    private final TournamentService tournamentService;
    private final TeamService teamService;

    public AdminTournamentController(TournamentService tournamentService, TeamService teamService) {
        this.tournamentService = tournamentService;
        this.teamService = teamService;
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("tournament", new Tournament());
        return "admin/tournaments/form";
    }

    @PostMapping
    public String save(@Valid @ModelAttribute("tournament") Tournament tournament,
                                   BindingResult bindingResult,
                                   @RequestParam("immagineFile") MultipartFile immagineFile,
                                   Model model) throws IOException {
        if (bindingResult.hasErrors()) {
        	model.addAttribute("formMode", "create");
            return "admin/tournaments/form";
        }
        
        if (!immagineFile.isEmpty()
	            && immagineFile.getSize() > 5 * 1024 * 1024) {

	        model.addAttribute(
	                "erroreImmagine",
	                "L'immagine selezionata supera la dimensione massima di 5 MB."
	        );
	        model.addAttribute("formMode", "create");

	        return "admin/tournaments/form";
	    }
        
        Image immagine = new Image();
		if (!immagineFile.isEmpty()) {
			immagine.setDati(immagineFile.getBytes());
			immagine.setContentType(immagineFile.getContentType());
			tournament.setImage(immagine);
		}
		else {
            try {
            	byte[] defaultImageData = new ClassPathResource("static/images/default.png").getInputStream().readAllBytes();
                immagine.setDati(defaultImageData);
                immagine.setContentType("image/png");
                tournament.setImage(immagine);
            } catch (IOException e) {
            	e.printStackTrace();
            }
        }

		try {
            this.tournamentService.save(tournament);
            return "redirect:/tournaments";
        } catch (DuplicateTournamentException e) {
            bindingResult.reject("tournament.duplicate", e.getMessage());
            model.addAttribute("formMode", "create");
            return "admin/tournaments/form";
        }
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Tournament tournament = this.tournamentService.findById(id);
        if (tournament == null) {
            return "redirect:/admin/tournaments";
        }
        model.addAttribute("formMode", "edit");
        model.addAttribute("tournament", tournament);
        return "admin/tournaments/form";
    }
    
    @GetMapping("/{id}/manage")
    public String manageTournament(@PathVariable("id") Long id, Model model) {
        Tournament tournament = this.tournamentService.findById(id);
        if (tournament == null) {
            return "redirect:/admin/tournaments/" + id + "/manage";
        }
        Set<Long> enrolledTeamIds = tournament.getTeams().stream()
                .map(Team::getId)
                .collect(Collectors.toSet());
        List<Team> availableTeams = this.teamService.findAll().stream()
                .filter(team -> !enrolledTeamIds.contains(team.getId()))
                .toList();
        model.addAttribute("availableTeams", availableTeams);
        model.addAttribute("tournament", tournament);
        return "admin/tournaments/manage";
    }
    
    @PostMapping("/{id}/teams")
    public String addTeam(@PathVariable("id") Long tournamentId, @RequestParam("teamId") Long teamId) {
        this.tournamentService.addTeamToTournament(tournamentId, teamId);
        return "redirect:/admin/tournaments/" + tournamentId + "/manage";
    }

    @PostMapping("/{id}/teams/{teamId}/remove")
    public String removeTeam(@PathVariable("id") Long tournamentId, @PathVariable("teamId") Long teamId) {
        this.teamService.removeTeamFromTournament(teamId, tournamentId);
        return "redirect:/admin/tournaments/" + tournamentId + "/manage";
    }

    @PostMapping("/{id}")
    public String updateTournament(@PathVariable Long id,
                                   @Valid @ModelAttribute("tournament") Tournament tournament,
                                   BindingResult bindingResult,
                                   @RequestParam(value = "immagineFile", required = false) MultipartFile immagineFile,
                                   Model model) throws IOException {

        if (immagineFile != null && !immagineFile.isEmpty() && immagineFile.getSize() > 5 * 1024 * 1024) {
            model.addAttribute("erroreImmagine", "L'immagine selezionata supera la dimensione massima di 5 MB.");
            model.addAttribute("formMode", "edit");
            return "admin/tournaments/form";
        }

        if (bindingResult.hasErrors()) {
            tournament.setId(id);
            Tournament existingTournament = this.tournamentService.findById(id);
            tournament.setImage(existingTournament.getImage());
            model.addAttribute("formMode", "edit");
            return "admin/tournaments/form";
        }

        try {
            this.tournamentService.update(id, tournament, immagineFile);
            return "redirect:/admin/tournaments/" + id + "/manage"; 
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "admin/tournaments/form";
        }
    }
}
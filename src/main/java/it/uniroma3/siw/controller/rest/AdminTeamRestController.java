package it.uniroma3.siw.controller.rest;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import it.uniroma3.siw.service.TeamService;

@RestController
@RequestMapping("/rest/admin/teams")
public class AdminTeamRestController {

    private final TeamService teamService;

    public AdminTeamRestController(TeamService teamService) {
        this.teamService = teamService;
    }

    @DeleteMapping("/bulk")
    public ResponseEntity<Void> deleteMultiple(@RequestBody List<Long> teamIds) {
        this.teamService.deleteAll(teamIds);
        return ResponseEntity.noContent().build();
    }
}
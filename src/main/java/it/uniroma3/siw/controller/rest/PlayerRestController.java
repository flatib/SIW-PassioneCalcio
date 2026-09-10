package it.uniroma3.siw.controller.rest;

import it.uniroma3.siw.model.Player;
import it.uniroma3.siw.service.PlayerService;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rest/players")
public class PlayerRestController {

    private PlayerService playerService;
    
    public PlayerRestController(PlayerService playerService) {
		this.playerService = playerService;
	}

    @GetMapping
    public Page<Player> getPlayers(@RequestParam(defaultValue = "0") int page,
                                   @RequestParam(defaultValue = "10") int size,
                                   @RequestParam(required = false) String search,
                                   @RequestParam(required = false) String role,
                                   @RequestParam(defaultValue = "false") boolean onlyFreeAgents) {
        return playerService.searchPlayersPaginated(search, role, onlyFreeAgents, page, size);
    }
}
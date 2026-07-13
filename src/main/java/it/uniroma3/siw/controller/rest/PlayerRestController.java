package it.uniroma3.siw.controller.rest;

import it.uniroma3.siw.model.Player;
import it.uniroma3.siw.service.PlayerService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rest/players")
public class PlayerRestController {

    private PlayerService playerService;
    
    public PlayerRestController(PlayerService playerService) {
		this.playerService = playerService;
	}

    @GetMapping
    public List<Player> getPlayers(@RequestParam(required = false) String search) {
        return playerService.searchPlayers(search);
    }
}
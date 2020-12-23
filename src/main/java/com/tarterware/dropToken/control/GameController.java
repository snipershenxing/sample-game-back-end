package com.tarterware.dropToken.control;

import com.alibaba.fastjson.JSON;
import com.tarterware.dropToken.entities.Game;
import com.tarterware.dropToken.service.GameService;
import com.tarterware.dropToken.entities.Move;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequestMapping("drop_token/")
@RestController
public class GameController {

	private final GameService gameService;

	@Autowired
	public GameController(GameService gameService1) {
		this.gameService = gameService1;
	}

	/* get all games' id */
	@GetMapping
	public String getAllGames() {
		List<String> allGames = gameService.getAllGame();
		Map<String, Object> map = new HashMap<>();
		map.put("games", allGames);
		return JSON.toJSONString(map, true);
	}

	/* create new game
	* pass list of players as parameters */
	@PostMapping
	public String createNewGame(@RequestBody List<String> players) {
		String success = gameService.createNewGame(players);
		Map<String, Object> map = new HashMap<>();
		map.put("gameId", success);
		return JSON.toJSONString(map, true);
}

//	@PostMapping
//	public String createNewGame(@RequestBody @Valid Game game) {
//		return gameService.createNewGame(game);
//	}

	@GetMapping(path = "{gameId}")
	public String getStateOfGameById(@PathVariable("gameId") String gameId) {
		Game curGame = gameService.getStateOfGameById(gameId);
		return JSON.toJSONString(curGame);
	}

	@GetMapping(path = "{gameId}/moves")
	public String getListOfMove(@PathVariable("gameId") String gameId) {
		Collection<Move> moves = gameService.getListOfMove(gameId);
		return JSON.toJSONString(moves, true);
	}

	@PostMapping(path = "{gameId}/{playerId}")
	public String postMove(@RequestBody int column,
						   @PathVariable("playerId") String playerId,
						   @PathVariable("gameId") String gameId)
			throws Exception {
		String curMove = gameService.postMove(gameId, playerId, column);
		Map<String, Object> map = new HashMap<>();
		map.put("move", curMove);
		return JSON.toJSONString(map, true);
	}

	@GetMapping(path = "{gameId}/moves/{move_number}")
	public String getMove(@PathVariable("gameId") String gameId,
						  @PathVariable("move_number") String moveNum) {
		int targetMoveNum = Integer.parseInt(moveNum);
		Move targetMove = gameService.getMove(gameId, targetMoveNum);
		return JSON.toJSONString(targetMove);
	}

	@DeleteMapping(path = "{gameId}/{playerId}")
	public void playerQuit(@PathVariable("gameId") String gameId,
						   @PathVariable("playerId") String playerId) {
		gameService.playerQuit(gameId, playerId);
	}
}

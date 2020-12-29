package com.tarterware.dropToken.control;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tarterware.dropToken.entities.Game;
import com.tarterware.dropToken.exceptions.ApiException;
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
	* pass list of players as parameters
	*/
	@PostMapping
	public String createNewGame(@RequestBody @Valid JSONObject game) throws ApiException.MalformedException {
		List<String> players = (List<String>) game.get("players");

		if (players.size() < 2 || game.get("columns") == null || game.get("rows") == null) {
			throw new ApiException.MalformedException("Malformed request");
		}

		String success = gameService.createNewGame(players);
		JSONObject result = new JSONObject();
		result.put("gameId", success);
		return result.toJSONString();
	}

	/*
	return game state searching by path variable
	need to check if gameId exist in the game list
	 */
	@GetMapping(path = "{gameId}")
	public String getStateOfGameById(@PathVariable("gameId") String gameId) throws Exception {
		if (gameId == null) {
			throw new ApiException.MalformedException("Malformed request");
		}
		Game curGame = gameService.getStateOfGameById(gameId);
		JSONObject result = new JSONObject();
		result.put("players", curGame.getPlayersId());
		result.put("state", curGame.getGameState());
		if (curGame.getGameState() != Game.GameState.IN_PROGRESS) {
			result.put("winner", curGame.getWinner());
		}

		return result.toJSONString();
	}

	@GetMapping(path = "{gameId}/moves")
	public String getListOfMove (@PathVariable("gameId") String gameId) throws Exception  {
		if (gameId == null) {
			throw new ApiException.MalformedException("Malformed request");
		}
		Collection<Move> moves = gameService.getListOfMove(gameId);
		if (moves.size() == 0) {
			throw new ApiException.GameNotFoundException("Game/moves not found");
		}
		return JSON.toJSONString(moves, true);
	}

	@PostMapping(path = "{gameId}/{playerId}")
	public String postMove(@RequestBody JSONObject input,
						   @PathVariable("playerId") String playerId,
						   @PathVariable("gameId") String gameId)
			throws Exception {
		int column = (int) input.get("column");

		if (input == null || column < 1 || column > 4) {
			throw new ApiException.IllegalMoveException("Malformed input. Illegal move");
		}

		String curMove = gameService.postMove(gameId, playerId, column);
		Map<String, Object> map = new HashMap<>();
		map.put("move", curMove);
		return JSON.toJSONString(map, true);
	}

	@GetMapping(path = "{gameId}/moves/{move_number}")
	public String getMove(@PathVariable("gameId") String gameId,
						  @PathVariable("move_number") String moveNum) throws Exception {
		if (gameId == null || moveNum == null) {
			throw new ApiException.MalformedException("Malformed request");
		}
		int targetMoveNum = Integer.parseInt(moveNum);
		Move targetMove = gameService.getMove(gameId, targetMoveNum);
		if (targetMove == null) {
			throw new ApiException.GameNotFoundException("Game/moves not found");
		}
		return JSON.toJSONString(targetMove);
	}

	@DeleteMapping(path = "{gameId}/{playerId}")
	public void playerQuit(@PathVariable("gameId") String gameId,
						   @PathVariable("playerId") String playerId) throws Exception {
		if (gameId == null || playerId == null) {
			throw new ApiException.MalformedException("Malformed request");
		}
		gameService.playerQuit(gameId, playerId);
	}
}

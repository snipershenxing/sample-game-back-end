package com.ethanliang.dropToken.control;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ethanliang.dropToken.service.GameService;
import com.ethanliang.dropToken.entities.Move;
import com.ethanliang.dropToken.exceptions.ApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("drop_token/")
public class GameController {

	private final GameService gameService;

	@Autowired
	public GameController(GameService gameService1) {
		this.gameService = gameService1;
	}

	/*
	Request all games' id
	return list of game id of all in-progress games
	*/
	@GetMapping
	public String getAllGames() {
		List<String> allGames = gameService.getAllGame();
		JSONObject result = new JSONObject();
		result.put("games", allGames);
		return result.toJSONString();
	}

	/*
	create new game
	get list of players' name from request body
	validate the inputs
	*/
	@PostMapping
	public String createNewGame(@RequestBody @Valid JSONObject game) throws ApiException.MalformedException {
		String success = gameService.createNewGame(game);
		JSONObject result = new JSONObject();
		result.put("gameId", success);
		return result.toJSONString();
	}

	/*
	return game state searching by path variable
	need to check if gameId exist in the game list
	 */
	@GetMapping(path = "{gameId}")
	public String getStateOfGameById(@PathVariable("gameId") String gameId) {
		if (gameId == null || !gameId.contains("gameId")) {
			throw new ApiException.MalformedException("Malformed request");
		}
		JSONObject result = gameService.getStateOfGameById(gameId);
		return result.toJSONString();
	}

	/*
	get all moves made in game
	searching by game ID
	 */
	@GetMapping(path = "{gameId}/moves")
	public String getListOfMove (@PathVariable("gameId") String gameId) {
		if (gameId == null || !gameId.contains("gameId")) {
			throw new ApiException.MalformedException("Malformed request");
		}
		Collection<Move> moves = gameService.getListOfMove(gameId);
		if (moves.size() == 0) {
			throw new ApiException.GameNotFoundException("Game/moves not found");
		}
		return JSON.toJSONString(moves, true);
	}

	/*
	post a move by player in game
	Searching by game ID and player ID
	 */
	@PostMapping(path = "{gameId}/{playerId}")
	public String postMove(@RequestBody JSONObject input,
						   @PathVariable("playerId") String playerId,
						   @PathVariable("gameId") String gameId) {
		if (!input.containsKey("column")) {
			throw new ApiException.IllegalMoveException("Malformed input. Illegal move");
		}
		int column = input.getInteger("column");

		if (column < 1 || column > 4 || !gameId.contains("gameId") || playerId.equals("")) {
			throw new ApiException.IllegalMoveException("Malformed input. Illegal move");
		}

		String curMove = gameService.postMove(gameId, playerId, column);
		JSONObject result = new JSONObject();
		result.put("move", curMove);
		return result.toJSONString();
	}

	/*
	get the specific move in a game
	Searching by game ID and number of move
	 */
	@GetMapping(path = "{gameId}/moves/{move_number}")
	public String getMove(@PathVariable("gameId") String gameId,
						  @PathVariable("move_number") String moveNum) {
		if (gameId == null || moveNum == null || !gameId.contains("gameId") || Integer.parseInt(moveNum) < 1) {
			throw new ApiException.MalformedException("Malformed request");
		}
		int targetMoveNum = Integer.parseInt(moveNum);
		Move targetMove = gameService.getMove(gameId, targetMoveNum);
		if (targetMove == null) {
			throw new ApiException.GameNotFoundException("Game/moves not found");
		}
		return JSON.toJSONString(targetMove);
	}

	/*
	player quit from a game
	 */
	@DeleteMapping(path = "{gameId}/{playerId}")
	public void playerQuit(@PathVariable("gameId") String gameId,
						   @PathVariable("playerId") String playerId) {
		if (gameId == null || playerId == null || !gameId.contains("gameId") || playerId.equals("")) {
			throw new ApiException.MalformedException("Malformed request");
		}
		gameService.playerQuit(gameId, playerId);
	}
}

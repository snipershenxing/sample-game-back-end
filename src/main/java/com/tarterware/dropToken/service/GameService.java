package com.tarterware.dropToken.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.tarterware.dropToken.entities.Game;
import com.tarterware.dropToken.entities.Player;
import com.tarterware.dropToken.exceptions.ApiException;
import com.tarterware.dropToken.repository.GameDataAccessService;
import com.tarterware.dropToken.entities.Move;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class GameService {

	private final GameDataAccessService gameDataAccessService;
	private final List<Integer> gameId;

	@Autowired
	public GameService()
	{
		this.gameDataAccessService = new GameDataAccessService();
		this.gameId = new ArrayList<>();
	}

	public List<String> getAllGame() {
		//get all games from repository
		List<Game> gs = gameDataAccessService.getAllGames();
		//get all game id from the games and put into a list
		List<String> games = new ArrayList<>();
		for (Game g : gs) {
			games.add(g.getGameId());
		}
		return games;
	}

	public String createNewGame(JSONObject game) {
		//get players' name
		if (!game.containsKey("players") || !game.containsKey("columns") || !game.containsKey("rows")) {
			throw new ApiException.MalformedException("Malformed request");
		}

		JSONArray players = game.getJSONArray("players");

		if (players == null || players.size() != 2 || game.get("columns") == null || game.get("rows") == null) {
			throw new ApiException.MalformedException("Malformed request");
		}

		String p1 = players.getString(0);
		String p2 = players.getString(1);

		if (p1.equals("") || p2.equals("")) {
			throw new ApiException.MalformedException("Malformed request");
		}

		//generate game id cumulatively
		int curId;
		if (gameId.size() == 0) {
			curId = 1;
		} else {
			int size = gameId.size();
			curId = gameId.get(size - 1) + 1;
		}
		gameId.add(curId);

		//create a new game
		Game curGame = new Game(p1, p2, curId);
		GameDataAccessService.createGame(curGame);
		return "gameId" + curId;
	}

	public JSONObject getStateOfGameById(String gameId) {
		Game curGame = gameDataAccessService.getGameById(gameId);
		JSONObject result = new JSONObject(new LinkedHashMap<>());
		result.put("players", curGame.getPlayersId());
		result.put("state", curGame.getGameState());
		if (curGame.getGameState() != Game.GameState.IN_PROGRESS) {
			result.put("winner", curGame.getWinner());
		}
		return result;
	}

	public Collection<Move> getListOfMove(String gameId) {
		Game curGame = gameDataAccessService.getGameById(gameId);
		Map<Integer, Move> moves = curGame.getListOfMove();
		return moves.values();
	}

	public String postMove(String gameId, String playerId, int column) {
		//get game by gameId from repository
		Game curGame = gameDataAccessService.getGameById(gameId);
		//check game status
		if (curGame.getGameState().equals(Game.GameState.DONE)) {
			throw new ApiException.DoneStateException("Game is already in DONE state");
		} else {
			//find player in this game and set the current player
			Player curGamePlayer = curGame.getPlayerById(playerId);
			curGame.setCurPlayer(curGamePlayer);
			//post move in the game
			curGame.postMove(column);
			//create new move class
			Move curMove = new Move(playerId, column);
			int curGameNumOfMove = curGame.getNumOfMove();
			curGameNumOfMove += 1;
			curGame.recordMove(curGameNumOfMove, curMove);
			curGame.setNumOfMove(curGameNumOfMove);
			//after posting move, check if there's any winner
			curGame.updateStatus();
			return gameId + "/moves/" + "moveNum: " + curGameNumOfMove;
		}
	}

	public Move getMove(String gameId, int moveNum) {
		Game curGame = gameDataAccessService.getGameById(gameId);
		Map<Integer, Move> moves = curGame.getListOfMove();
		if (!moves.containsKey(moveNum)) {
			throw new ApiException.GameNotFoundException("Game/moves not found");
		}
		return moves.get(moveNum);
	}

	public void playerQuit(String gameId, String playerId) {
		Game curGame = gameDataAccessService.getGameById(gameId);
		if (curGame == null) {
			throw new ApiException.PlayerNotFoundException("Game not found or player is not a part of it");
		}
		if (curGame.getGameState().equals(Game.GameState.DONE)) {
			System.out.println("work");
			throw new ApiException.DoneStateException("Game is already in DONE state");
		}
		List<Player> curGamePlayers = curGame.getPlayers();
		List<String> curGamePlayersId = curGame.getPlayersId();
		if (!curGamePlayersId.contains(playerId)) {
			throw new ApiException.PlayerNotFoundException("Game not found or player is not a part of it");
		}
		//record this move in the game
		curGame.setNumOfMove(curGame.getNumOfMove() + 1);
		Move curMove = new Move(playerId);
		curGame.recordMove(curGame.getNumOfMove(), curMove);
		//delete the player from the game
		Player curGamePlayer = curGame.getPlayerById(playerId);
		curGamePlayersId.remove(playerId);
		curGamePlayers.remove(curGamePlayer);
		//set game state
		curGame.setGameState(Game.GameState.DONE);
	}
}

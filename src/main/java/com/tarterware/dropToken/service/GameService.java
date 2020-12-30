package com.tarterware.dropToken.service;

import com.alibaba.fastjson.JSONObject;
import com.tarterware.dropToken.entities.Game;
import com.tarterware.dropToken.entities.Player;
import com.tarterware.dropToken.exceptions.ApiException;
import com.tarterware.dropToken.repository.GameDataAccessService;
import com.tarterware.dropToken.entities.Move;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

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
		List<Game> gs = gameDataAccessService.getAllGames();
		List<String> games = new ArrayList<>();
		for (Game g : gs) {
			games.add(g.getId());
		}
		return games;
	}

	public String createNewGame(List<String> players) {
		int curId;
		if (gameId.size() == 0) {
			curId = 1;
		} else {
			int size = gameId.size();
			curId = gameId.get(size - 1) + 1;
		}
		gameId.add(curId);
		Game curGame = new Game(players.get(0), players.get(1), curId);
		GameDataAccessService.createGame(curGame);
		return "gameId" + curId;
	}

	public JSONObject getStateOfGameById(String gameId) {
		Game curGame = gameDataAccessService.getGameById(gameId);
		JSONObject result = new JSONObject();
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

	public String postMove(String gameId, String playerId, int column) throws Exception {
		Game curGame = gameDataAccessService.getGameById(gameId);
		if (curGame.getGameState().equals(Game.GameState.DONE)) {
			throw new ApiException.DoneStateException("Game is already in DONE state");
		} else {
			Player curGamePlayer = curGame.getPlayerById(playerId);
			curGame.setCurGamePlayer(curGamePlayer);
			curGame.setMove(column);
			Move curMove = new Move(playerId, column);
			int curGameNumOfMove = curGame.getNumOfMove();
			curGameNumOfMove += 1;
			curGame.recordMove(curGameNumOfMove, curMove);
			curGame.setNumOfMove(curGameNumOfMove);
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
		List<String> curGamePlayersId = curGame.getPlayersId();
		if (!curGamePlayersId.contains(playerId)) {
			throw new ApiException.PlayerNotFoundException("Game not found or player is not a part of it");
		}
		curGame.setNumOfMove(curGame.getNumOfMove() + 1);
		Move curMove = new Move(playerId);
		curGame.recordMove(curGame.getNumOfMove(), curMove);
		Player curGamePlayer = curGame.getPlayerById(playerId);
		curGamePlayersId.remove(playerId);
		List<Player> curGamePlayers = curGame.getPlayers();
		curGamePlayers.remove(curGamePlayer);
		curGame.setGameState(Game.GameState.DONE);
	}
}

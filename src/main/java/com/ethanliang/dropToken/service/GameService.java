package com.ethanliang.dropToken.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ethanliang.dropToken.repository.GameDataAccessService;
import com.ethanliang.dropToken.entities.Game;
import com.ethanliang.dropToken.exceptions.ApiException;
import com.ethanliang.dropToken.entities.Move;
import groovy.util.logging.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class GameService {

	private final GameDataAccessService gameDataAccessService;
	private final List<Integer> gameId;

	@Autowired
	public GameService()
	{
		this.gameDataAccessService = new GameDataAccessService();
		this.gameId = Collections.synchronizedList(new ArrayList<>());
	}

	public List<String> getAllGame() {
		//get all games from repository
		Collection<Game> gs = gameDataAccessService.getAllGames();
		//get all game id from the games and put into a list
		List<String> games = new ArrayList<>();
		for (Game g : gs) {
			if (g.getGameState() == Game.GameState.IN_PROGRESS) {
				games.add(g.getGameId());
			}
		}
		return games;
	}
	@Async("dropTokenExecutor")
	public CompletableFuture<String> createNewGame(JSONObject game) throws InterruptedException {
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

		if (p1.equals("") || p2.equals("") || p1.equals(p2)) {
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
		GameDataAccessService.addGame(curGame);
		Thread.sleep(0);
		return CompletableFuture.completedFuture("gameId" + curId);
	}

	public JSONObject getStateOfGameById(String gameId) {
		Game curGame = gameDataAccessService.getGameById(gameId);
		JSONObject result = new JSONObject(new LinkedHashMap<>());
		result.put("players", curGame.getPlayerIds());
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
			Set<String> playerIds = curGame.getPlayerIds();
			if (!playerIds.contains(playerId)) {
				throw new ApiException.PlayerNotFoundException("Game not found or player is not a part of it");
			}
			String lastPlayer = curGame.getCurPlayer();
			if (lastPlayer.equals(playerId)) {
				throw new ApiException.WrongTurnException("Player tried to post when it's not their turn");
			}
			String firstPlayer = curGame.getFirstPlayer();
			//post move in the game
			Game.Marker[][] curBoard = curGame.getBoard();
			markAt(curBoard, column, playerId, firstPlayer);
			curGame.setCurPlayer(playerId);
			//create new move class
			Move curMove = new Move(playerId, column);
			curGame.recordMove(curGame.getListOfMove().size() + 1, curMove);
			//after posting move, check if there's any winner
			if (isWin(curBoard)) {
				curGame.setGameState(Game.GameState.DONE);
				curGame.setWinner(playerId);
			} else {
				if (isDraw(curBoard)) {
					curGame.setGameState(Game.GameState.DONE);
					curGame.setWinner(null);
				} else {
					curGame.setGameState(Game.GameState.IN_PROGRESS);
				}
			}
			curGame.setBoard(curBoard);
			return gameId + "/moves/" + "moveNum: " + curGame.getListOfMove().size();
		}
	}

	private boolean isDraw(Game.Marker[][] curBoard) {
		for(int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				if (curBoard[i][j] == Game.Marker.BLANK) {
					return false;
				}
			}
		}
		return true;
	}

	private boolean isWin(Game.Marker[][] curBoard) {
		Game.Marker flag;
		//x, y
		for (int i = 0; i < 4; i++) {
			flag = curBoard[i][i];
			if (flag == Game.Marker.BLANK) {
				continue;
			}
			boolean xFull = true;
			boolean yFull = true;
			//x dir
			for (int j = 0; j < 4; j++) {
				if (curBoard[i][j] != flag || curBoard[i][j] == Game.Marker.BLANK) {
					xFull = false;
				}
				if (curBoard[j][i] != flag || curBoard[j][i] == Game.Marker.BLANK) {
					yFull = false;
				}
			}
			if (xFull || yFull) {
				return true;
			}
		}
		//diagnol 1
		boolean diagonal1 = true;
		boolean diagonal2 = true;
		flag = curBoard[0][0];
		if (curBoard[0][0] == Game.Marker.BLANK) {
			diagonal1 = false;
		} else {
			for (int i = 3; i >= 0; i--) {
				if (curBoard[i][i] != flag) {
					diagonal1 = false;
					break;
				}
			}
		}

		//diagnol 2
		flag = curBoard[3][0];
		if (curBoard[3][0] == Game.Marker.BLANK) {
			diagonal2 = false;
		} else {
			for (int i = 3; i >= 0; i--) {
				if (curBoard[i][3 - i] != flag) {
					diagonal2 = false;
					break;
				}
			}
		}

		return diagonal1 || diagonal2;
	}

	private void markAt(Game.Marker[][] curBoard, int column, String curPlayer, String firstPlayer) {
		column -= 1;
		if (curBoard[0][column] != Game.Marker.BLANK) {
			throw new ApiException.IllegalMoveException("Malformed input. Illegal move");
		} else {
			for (int i = 3; i >= 0; i--) {
				if (curBoard[i][column] == Game.Marker.BLANK) {
					if (curPlayer.equals(firstPlayer)) {
						curBoard[i][column] = Game.Marker.RED;
					} else {
						curBoard[i][column] = Game.Marker.BLUE;
					}
					break;
				}
			}
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
			throw new ApiException.DoneStateException("Game is already in DONE state");
		}
		Set<String> curGamePlayers = curGame.getPlayerIds();
		if (!curGamePlayers.contains(playerId)) {
			throw new ApiException.PlayerNotFoundException("Game not found or player is not a part of it");
		}
		//record this move in the game
		Move curMove = new Move(playerId);
		curGame.recordMove(curGame.getListOfMove().size() + 1, curMove);
		//delete the player from the game
		curGamePlayers.remove(playerId);
		curGame.setPlayerIds(curGamePlayers);
		//set game state
		curGame.setGameState(Game.GameState.DONE);
	}
}

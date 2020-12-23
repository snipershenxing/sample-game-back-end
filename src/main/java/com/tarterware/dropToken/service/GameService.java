package com.tarterware.dropToken.service;

import com.tarterware.dropToken.entities.Game;
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

	@Autowired
	public GameService()
	{
		this.gameDataAccessService = new GameDataAccessService();
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
		return gameDataAccessService.createGame(players);
	}

//	public String createNewGames(Game game) {
//		int curId;
//		List<Integer> gameIdList = GameDataAccessService.getAllGameIds();
//		List<Game> gameList = GameDataAccessService.getAllGame();
//		if (gameIdList.size() == 0) {
//			curId = 1;
//		} else {
//			curId = gameIdList.get(gameIdList.size() - 1) + 1;
//		}
//		gameIdList.add(curId);
//		gameDataAccessService.setAllGameIds(gameIdList);
//	}

	public Game getStateOfGameById(String gameId) {
		return gameDataAccessService.getStateById(gameId);
	}

	public Collection<Move> getListOfMove(String gameId) {
		Map<Integer, Move> moves = gameDataAccessService.getListOfMove(gameId);
		return moves.values();
	}

	public String postMove(String gameId, String playerId, int column) throws Exception {
		int moveNum = gameDataAccessService.postMove(gameId, playerId, column);
		return gameId + "/moves/" + "moveNum: " + moveNum;
	}

	public Move getMove(String gameId, int moveNum) {
		Map<Integer, Move> moves = gameDataAccessService.getListOfMove(gameId);
		return moves.get(moveNum);
	}

	public void playerQuit(String gameId, String playerId) {
		gameDataAccessService.playerQuit(gameId, playerId);
	}
}

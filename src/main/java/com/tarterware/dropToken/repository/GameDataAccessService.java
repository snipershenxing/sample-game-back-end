package com.tarterware.dropToken.repository;

import com.tarterware.dropToken.entities.Game;
import com.tarterware.dropToken.entities.Move;
import com.tarterware.dropToken.exceptions.ApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository("games")
public class GameDataAccessService {

    private final List<Game> games;
    private final List<Integer> gameId;
    private final List<Integer> playerId;
    private Game curGame = null;

    @Autowired
    public GameDataAccessService() {
        this.games = new ArrayList<>();
        this.gameId = new ArrayList<>();
        this.playerId = new ArrayList<>();
    }

    public List<Game> getAllGames() {
        return this.games;
    }

    public String createGame(List<String> players) {
        int curId;
        if (gameId.size() == 0) {
            curId = 1;
        } else {
            int size = gameId.size();
            curId = gameId.get(size - 1) + 1;
        }
        gameId.add(curId);
        games.add(new Game(players.get(0), players.get(1), curId));

        return "gameid" + curId;
    }

    public Game getStateById(String gameId) {
        //get current game
        checkCurGame(gameId);
        return curGame;
    }

    public Map<Integer, Move> getListOfMove(String gameId) {
        checkCurGame(gameId);
        return curGame.getListOfMove();
    }

    private void checkCurGame(String gameId) {
        if (curGame != null) {
            curGame = null;
        }
        Optional<Game> catchCurGame = games.stream()
                .filter(game -> game.getId().equals(gameId))
                .findFirst();
        catchCurGame.ifPresent(game -> curGame = game);
        if (curGame == null) {
            throw new ApiException.GameNotFoundException("Game/moves not found");
        }
    }

    public int postMove(String gameId, String playerId, int column) throws Exception {
        if (curGame != null) {
            curGame = null;
        }
        Optional<Game> catchCurGame = games.stream()
                .filter(game -> game.getId().equals(gameId))
                .findFirst();
        catchCurGame.ifPresent(game -> curGame = game);
        if (curGame == null || curGame.getGameState().equals(Game.GameState.DONE)) {
            throw new ApiException.PlayerNotFoundException("Game not found or player is not a part of it");
        }
        //make move, set player, mark on board
        int moveNum = curGame.postMove(playerId, column);
        curGame.updateStatus();
        return moveNum;
    }

    public void playerQuit(String gameId, String playerId) throws Exception {
        if (curGame != null) {
            curGame = null;
        }
        Optional<Game> catchCurGame = games.stream()
                .filter(game -> game.getId().equals(gameId))
                .findFirst();
        catchCurGame.ifPresent(game -> curGame = game);
        if (curGame == null) {
            throw new ApiException.PlayerNotFoundException("Game not found or player is not a part of it");
        }
        if (curGame.getGameState().equals(Game.GameState.DONE)) {
            System.out.println("work");
            throw new ApiException.DoneStateException("Game is already in DONE state");
        }
        curGame.playerQuit(playerId);
    }
}

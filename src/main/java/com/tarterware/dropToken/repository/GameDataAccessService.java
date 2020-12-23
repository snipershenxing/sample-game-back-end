package com.tarterware.dropToken.repository;

import com.tarterware.dropToken.entities.Game;
import com.tarterware.dropToken.entities.Move;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository("games")
public class GameDataAccessService {

    private final List<Game> games;
    private final List<Integer> gameId;
    private final List<Integer> playerId;
    private Game curGame;

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
        Optional<Game> catchCurGame = games.stream()
                .filter(game -> game.getId().equals(gameId))
                .findFirst();
        catchCurGame.ifPresent(game -> curGame = game);
        //set game state
        String winnerChecking = curGame.checkWinner();
        if (curGame.getGameState().equals(Game.GameState.DONE)) {
            curGame.setGameState("Done");
        } else {
            if (!winnerChecking.equalsIgnoreCase("inprogress")) {
                curGame.setGameState("Done");
            } else {
                curGame.setGameState("inProgress");
            }
        }

        //set Winner
//        if (winnerChecking.equals("inProgress") || winnerChecking.equals("draw")) {
//            //do nothing
//        } else {
//            curGame.setWinner(winnerChecking);
//        }
        curGame.setWinner(winnerChecking);

        return curGame;
    }

    public Map<Integer, Move> getListOfMove(String gameId) {
        Optional<Game> catchCurGame = games.stream()
                .filter(game -> game.getId().equals(gameId))
                .findFirst();
        catchCurGame.ifPresent(game -> curGame = game);
        return curGame.getListOfMove();
    }

    public int postMove(String gameId, String playerId, int column) throws Exception {
        Optional<Game> catchCurGame = games.stream()
                .filter(game -> game.getId().equals(gameId))
                .findFirst();
        catchCurGame.ifPresent(game -> curGame = game);
        //make move, set player, mark on board
        return curGame.postMove(playerId, column);
    }

    public void playerQuit(String gameId, String playerId) {
        Optional<Game> catchCurGame = games.stream()
                .filter(game -> game.getId().equals(gameId))
                .findFirst();
        catchCurGame.ifPresent(game -> curGame = game);
        curGame.playerQuit(playerId);
    }
}

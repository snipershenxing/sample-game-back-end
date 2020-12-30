package com.tarterware.dropToken.repository;

import com.tarterware.dropToken.entities.Game;
import com.tarterware.dropToken.exceptions.ApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository("games")
public class GameDataAccessService {

    private final static List<Game> games = new ArrayList<>();
    private Game curGame = null;

    @Autowired
    public GameDataAccessService() {

    }

    public List<Game> getAllGames() {
        return games;
    }

    public static void createGame(Game game) {
        games.add(game);
    }

    public Game getGameById(String gameId) {
        if (curGame != null) {
            curGame = null;
        }
        Optional<Game> catchCurGame = games.stream()
                .filter(game -> game.getGameId().equals(gameId))
                .findFirst();
        catchCurGame.ifPresent(game -> curGame = game);
        if (curGame == null) {
            throw new ApiException.GameNotFoundException("Game/moves not found");
        }
        return curGame;
    }
}

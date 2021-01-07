package com.ethanliang.dropToken.repository;

import com.ethanliang.dropToken.entities.Game;
import com.ethanliang.dropToken.exceptions.ApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository("games")
public class GameDataAccessService {

    private final static HashMap<String, Game> gameDB = new HashMap<>();

    @Autowired
    public GameDataAccessService() {}

    public static void addGame(Game game) {
        gameDB.put(game.getGameId(), game);
    }

    public Game getGameById(String gameId) {
        if (!gameDB.containsKey(gameId)) {
            throw new ApiException.GameNotFoundException("Game/moves not found");
        } else {
            return gameDB.get(gameId);
        }
    }

    public Collection<Game> getAllGames() {
        return gameDB.values();
    }
}

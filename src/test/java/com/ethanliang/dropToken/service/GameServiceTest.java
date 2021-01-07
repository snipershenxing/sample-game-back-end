package com.ethanliang.dropToken.service;

import com.alibaba.fastjson.JSONObject;
import com.ethanliang.dropToken.entities.Game;
import com.ethanliang.dropToken.entities.Move;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GameServiceTest extends TestCase {

    @Autowired
    private GameService gameservice;

    @Test
    public void getAllGamesTest() {
        List<String> rstOfAllGame = gameservice.getAllGame();
        System.out.println("-----------------------------------------");
        System.out.println(rstOfAllGame);
        System.out.println("-----------------------------------------");
    }

    @Test
    public void createNewGameTest() {
        JSONObject testRequest = new JSONObject();
        List<String> testPlayers = new ArrayList<>();
        testPlayers.add("Ethan");
        testPlayers.add("Ellen");
        testRequest.put("players", testPlayers);
        testRequest.put("columns", 4);
        testRequest.put("rows", 4);
        String rstOfCreatingGame = gameservice.createNewGame(testRequest);
        System.out.println("-----------------------------------------");
        System.out.println(rstOfCreatingGame);
        System.out.println("-----------------------------------------");

        List<String> rstOfAllGame = gameservice.getAllGame();
        System.out.println("-----------------------------------------");
        System.out.println(rstOfAllGame);
        System.out.println("-----------------------------------------");

        String testGameId = "gameId1";
        JSONObject rstOfGettingGameState = gameservice.getStateOfGameById(testGameId);
        System.out.println("-----------------------------------------");
        System.out.println(rstOfGettingGameState);
        System.out.println("-----------------------------------------");

        String rstOfPostMove = gameservice.postMove("gameId1", "Ethan", 1);
        System.out.println("-----------------------------------------");
        System.out.println(rstOfPostMove);
        System.out.println("-----------------------------------------");

        Collection<Move> rstOfGettingMoveList = gameservice.getListOfMove("gameId1");
        System.out.println("-----------------------------------------");
        System.out.println(rstOfGettingMoveList);
        System.out.println("-----------------------------------------");

        Move rstOfGettingMove = gameservice.getMove("gameId1", 1);
        System.out.println("-----------------------------------------");
        System.out.println(rstOfGettingMove);
        System.out.println("-----------------------------------------");

        gameservice.playerQuit("gameId1", "Ethan");
        System.out.println("-----------------------------------------");
        System.out.println(gameservice.getStateOfGameById("gameId1"));
        System.out.println("-----------------------------------------");
    }

    @Test
    public void getGameStateByIdTest() {
        String testGameId = "gameId1";
        JSONObject rstOfGettingGameState = gameservice.getStateOfGameById(testGameId);
        System.out.println("-----------------------------------------");
        System.out.println(rstOfGettingGameState);
        System.out.println("-----------------------------------------");
    }

    @Test
    public void isWinTest() {
        Game.Marker[][] boardTest = new Game.Marker[4][4];
        for (int i = 0; i < 4; i++) {
            boardTest[i][i] = Game.Marker.RED;
        }
        System.out.println("-----------------------------------------");
//        System.out.println(gameservice.isWin(boardTest));
        System.out.println("-----------------------------------------");
    }
}
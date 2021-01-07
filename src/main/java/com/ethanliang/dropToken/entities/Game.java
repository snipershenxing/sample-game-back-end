package com.ethanliang.dropToken.entities;

import java.util.*;

public class Game {

    public enum GameState{
        DONE, IN_PROGRESS
    }

    public enum Marker{
        RED, BLUE, BLANK
    }

    private Marker[][] board;

    private final String gameId;

    private Set<String> playerIds = new HashSet<>();

    private GameState gameState;

    private String curPlayer;

    private String winner;

    private final String firstPlayer;

    private final Map<Integer, Move> moveRecord;

    //initial a game
    public Game(String player1, String player2, int curId) {
        super();
        this.gameId = "gameId" + curId;

        playerIds.add(player1);
        playerIds.add(player2);

        this.gameState = GameState.IN_PROGRESS;

        this.moveRecord = new HashMap<>();

        this.curPlayer = "";

        this.firstPlayer = player1;

        this.board = new Marker[4][4];
        for(int r = 0;  r < 4;  ++r ) {
            for(int c = 0;  c < 4;  ++c) {
                board[r][c] = Marker.BLANK;
            }
        }
    }

    public String getGameId() {
        return this.gameId;
    }

    public Set<String> getPlayerIds() {
        return this.playerIds;
    }

    public void setPlayerIds(Set<String> playerIds) {
        this.playerIds = playerIds;
    }

    public GameState getGameState() {
        return this.gameState;
    }

    public void setGameState(GameState state) {
        this.gameState = state;
    }

    public String getWinner() {
        return this.winner;
    }

    public void setWinner(String player) {
        this.winner = player;
    }

    public String getFirstPlayer() {
        return this.firstPlayer;
    }

    public Marker[][] getBoard() {
        return board;
    }

    public void setBoard(Marker[][] board) {
        this.board = board;
    }

    public Map<Integer, Move> getListOfMove() {
        return this.moveRecord;
    }

    public void recordMove(int curGameNumOfMove, Move curMove) {
        this.moveRecord.put(curGameNumOfMove, curMove);
    }

    public void setCurPlayer(String curGamePlayer) {
        this.curPlayer = curGamePlayer;
    }

    public String getCurPlayer() {
        return this.curPlayer;
    }
}

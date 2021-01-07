package com.ethanliang.dropToken.entities;

import java.util.*;

public class Game {

    public enum GameState{
        DONE, IN_PROGRESS
    }

    private final String gameId;

    private Set<String> playerIds = new HashSet<>();

    private GameState gameState;

    private String winner;

    private final Map<Integer, Move> moveRecord;

    private final Board board;

    //initial a game
    public Game(String player1, String player2, int curId) {
        super();
        this.gameId = "gameId" + curId;

        playerIds.add(player1);
        playerIds.add(player2);

        this.gameState = GameState.IN_PROGRESS;

        this.board = new Board(player1);

        this.moveRecord = new HashMap<>();
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

    public void updateStatus() {
        if (board.checkWin()) {
            this.gameState = GameState.DONE;
            this.winner = board.getWinner();
        } else {
            if (board.isDraw()) {
                this.gameState = GameState.DONE;
                this.winner = null;
            } else {
                this.gameState = GameState.IN_PROGRESS;
            }
        }
    }

    public Map<Integer, Move> getListOfMove() {
        return this.moveRecord;
    }

    public void recordMove(int curGameNumOfMove, Move curMove) {
        this.moveRecord.put(curGameNumOfMove, curMove);
    }

    public void postMove(int column) {
        board.markAt(column);
    }

    public void setCurPlayer(String curGamePlayer) {
        board.setCurPlayer(curGamePlayer);
    }
}

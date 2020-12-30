package com.tarterware.dropToken.entities;

import com.alibaba.fastjson.annotation.JSONField;
import com.tarterware.dropToken.exceptions.ApiException;

import java.util.*;

public class Game {

    public enum GameState{
        DONE, IN_PROGRESS
    }

    @JSONField(serialize = false)
    private final String gameId;

    @JSONField(name = "players", ordinal = 1)
    private final List<String> playersId;

    @JSONField(name = "state", ordinal = 2)
    private GameState gameState;

    @JSONField(name = "winner", ordinal = 3)
    private String winner;

    @JSONField(serialize = false)
    private final Map<Integer, Move> moveRecord;

    private final Board board;

    private int numOfMove;

    private Player curPlayer = null;

    private final List<Player> players;

    //initial a game
    public Game(String player1, String player2, int curId) {
        super();
        this.gameId = "gameid" + curId;

        Player player11 = new Player(player1);
        Player player21 = new Player(player2);

        this.playersId = new ArrayList<>();
        playersId.add(player11.getPlayerId());
        playersId.add(player21.getPlayerId());

        this.players = new ArrayList<>();
        players.add(player11);
        players.add(player21);

        this.gameState = GameState.IN_PROGRESS;

        this.board = new Board(player11, player21);

        this.moveRecord = new HashMap<>();
        this.numOfMove = 0;
    }

    public String getId() {
        return this.gameId;
    }

    public GameState getGameState() {
        return this.gameState;
    }

    public void setGameState(GameState state) {
        this.gameState = state;
    }

    public List<String> getPlayersId() {
        return this.playersId;
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

    public int getNumOfMove() {
        return this.numOfMove;
    }

    public void setNumOfMove(int curGameNumOfMove) {
        this.numOfMove = curGameNumOfMove;
    }

    public void setMove(int column) throws Exception {
        board.markAt(column);
    }

    public Player getPlayerById(String playerId) {
        if (curPlayer != null) {
            curPlayer = null;
        }
        Optional<Player> catchCurPlayer = players.stream()
                .filter(player -> player.getPlayerId().equals(playerId))
                .findFirst();
        catchCurPlayer.ifPresent(player -> curPlayer = player);
        if (curPlayer == null || !playersId.contains(curPlayer.getPlayerId())) {
            throw new ApiException.PlayerNotFoundException("Game not found or player is not a part of it");
        }
        return curPlayer;
    }

    public void setCurGamePlayer(Player curGamePlayer) throws Exception {
        board.setCurPlayer(curGamePlayer);
    }

    public List<Player> getPlayers() {
        return this.players;
    }
}

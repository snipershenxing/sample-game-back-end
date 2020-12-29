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

    private Player curPlayer;
    private final Player player1;
    private final Player player2;

    private final List<Player> players;

    //initial a game
    public Game(String player1, String player2, int curId) {
        super();
        this.gameId = "gameid" + curId;
        this.player1 = new Player(player1);
        this.player2 = new Player(player2);
        this.playersId = new ArrayList<>();
        playersId.add(this.player1.getPlayerId());
        playersId.add(this.player2.getPlayerId());
        this.gameState = GameState.IN_PROGRESS;
        this.board = new Board(this.player1, this.player2);
        this.moveRecord = new HashMap<>();
        this.numOfMove = 0;
        this.players = new ArrayList<>();
        players.add(this.player1);
        players.add(this.player2);
    }

    public String getId() {
        return this.gameId;
    }

    public void setGameState(String state) {
        if (!state.equalsIgnoreCase("inProgress")) {
            this.gameState = GameState.DONE;
        } else {
            this.gameState = GameState.IN_PROGRESS;
        }
    }

    public GameState getGameState() {
        return this.gameState;
    }

    public List<String> getPlayersId() {
        return this.playersId;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }

    public String getWinner() {
        return this.winner;
    }

    public String checkWinner() {
//        boolean isWin = board.checkWin();
        boolean isWin = false;
        String winner;
        if (isWin) {
            winner = board.getWinner();
            return winner;
        } else {
            boolean isDraw = board.isDraw();
            if (isDraw) {
                return "draw";
            } else {
                return "inProgress";
            }
        }
    }

    public Map<Integer, Move> getListOfMove() {
        return this.moveRecord;
    }

    public int postMove(String playerId, int column) throws Exception {
        this.numOfMove += 1;
        //throw exception if player not in here
        Optional<Player> catchCurPlayer = players.stream()
                .filter(player -> player.getPlayerId().equals(playerId))
                .findFirst();
        catchCurPlayer.ifPresent(player -> curPlayer = player);
        if (curPlayer == null || !playersId.contains(curPlayer.getPlayerId())) {
            throw new ApiException.PlayerNotFoundException("Game not found or player is not a part of it");
        }
        //set current player & mark on the board
        board.setCurPlayer(curPlayer);
        board.markAt(column);
        //check if there is a winner

        //set current move
        Move curMove = new Move(playerId, column);
        moveRecord.put(numOfMove, curMove);
        return this.numOfMove;
    }

    public void playerQuit(String playerId) throws Exception {
        if (!playersId.contains(playerId)) {
            throw new ApiException.PlayerNotFoundException("Game not found or player is not a part of it");
        }
        numOfMove += 1;
        Move curMove = new Move(playerId);
        moveRecord.put(numOfMove, curMove);
        Optional<Player> catchCurPlayer = players.stream()
                .filter(player -> player.getPlayerId().equals(playerId))
                .findFirst();
        catchCurPlayer.ifPresent(player -> curPlayer = player);
        playersId.remove(playerId);
        this.gameState = GameState.DONE;
        this.winner = playerId.equalsIgnoreCase(player1.getPlayerId()) ? player2.getPlayerId() : player1.getPlayerId();
    }
}

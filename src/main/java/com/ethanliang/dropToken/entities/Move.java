package com.ethanliang.dropToken.entities;

import com.alibaba.fastjson.annotation.JSONField;

import javax.validation.constraints.NotBlank;

public class Move {
    enum MoveType{
        MOVE,
        QUIT
    }

    @JSONField(name = "type", ordinal = 1)
    private final MoveType moveType;

    @NotBlank
    @JSONField(name = "player", ordinal = 2)
    private final String playerId;

    @JSONField(name = "column", ordinal = 3)
    private int column;

    public Move(String playerId, int column) {
        this.moveType = MoveType.MOVE;
        this.playerId = playerId;
        this.column = column;
    }

    public Move(String playerId) {
        this.moveType = MoveType.QUIT;
        this.playerId = playerId;
    }

    public int getColumn() {
        return this.column;
    }

    public String getPlayerId() {
        return this.playerId;
    }

    public MoveType getMoveType(){
        return this.moveType;
    }

    @Override
    public String toString() {
        return "["+"Type: " + moveType
                + " Player: " + playerId
                + " column: " + column;
    }
}

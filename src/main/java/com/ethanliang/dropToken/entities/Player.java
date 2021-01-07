package com.ethanliang.dropToken.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotBlank;

public class Player {

    @NotBlank
    private final String playerId;

    public Player(@JsonProperty("playerId") String playerId) {
        this.playerId = playerId;
    }

    public String getPlayerId() {
        return this.playerId;
    }

    @Override
    public String toString() {
        return playerId;
    }
}

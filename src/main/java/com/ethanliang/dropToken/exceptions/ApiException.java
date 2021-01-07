package com.ethanliang.dropToken.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class ApiException {

    @ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Malformed request")
    public static class MalformedException extends RuntimeException{
        public MalformedException(String message) {
            super(message);
        }
    }

    @ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Game/moves not found")
    public static class GameNotFoundException extends RuntimeException{
        public GameNotFoundException(String message) {
            super(message);
        }
    }

    @ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Game/player not found")
    public static class PlayerNotFoundException extends RuntimeException{
        public PlayerNotFoundException(String message) {
            super(message);
        }
    }

    @ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Malformed input. Illegal move")
    public static class IllegalMoveException extends RuntimeException{
        public IllegalMoveException(String message) {
            super(message);
        }
    }

    @ResponseStatus(code = HttpStatus.CONFLICT, reason = "Player tried to post when it's not their turn")
    public static class WrongTurnException extends RuntimeException{
        public WrongTurnException(String message) {
            super(message);
        }
    }

    @ResponseStatus(code = HttpStatus.GONE, reason = "Game is already in DONE state")
    public static class DoneStateException extends RuntimeException{
        public DoneStateException(String message) {
            super(message);
        }
    }
}

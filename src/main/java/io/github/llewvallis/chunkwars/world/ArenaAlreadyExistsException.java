package io.github.llewvallis.chunkwars.world;

public class ArenaAlreadyExistsException extends RuntimeException {

    public ArenaAlreadyExistsException() {
        super();
    }

    public ArenaAlreadyExistsException(String message) {
        super(message);
    }

    public ArenaAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }

    public ArenaAlreadyExistsException(Throwable cause) {
        super(cause);
    }
}

package io.github.llewvallis.chunkwars.world;

import lombok.Getter;

public class NoSuchArenaException extends RuntimeException {

    @Getter
    private final String name;

    public NoSuchArenaException(String name) {
        super();
        this.name = name;
    }

    public NoSuchArenaException(String name, String message) {
        super(message);
        this.name = name;
    }

    public NoSuchArenaException(String name, String message, Throwable cause) {
        super(message, cause);
        this.name = name;
    }

    public NoSuchArenaException(String name, Throwable cause) {
        super(cause);
        this.name = name;
    }
}

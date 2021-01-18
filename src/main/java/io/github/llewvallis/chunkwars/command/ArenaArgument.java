package io.github.llewvallis.chunkwars.command;

import io.github.llewvallis.chunkwars.world.ArenaPool;
import io.github.llewvallis.chunkwars.world.NamedArena;
import io.github.llewvallis.commandbuilder.ArgumentParseException;
import io.github.llewvallis.commandbuilder.ArgumentParser;
import io.github.llewvallis.commandbuilder.CommandContext;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ArenaArgument implements ArgumentParser<NamedArena> {

    @Override
    public NamedArena parse(String argument, int position, CommandContext context) throws ArgumentParseException {
        return ArenaPool.instance.get(argument)
                .map(arena -> new NamedArena(argument, arena))
                .orElseThrow(() -> new ArgumentParseException("That arena does not exist"));
    }

    @Override
    public Set<String> complete(List<Object> parsedArguments, String currentArgument, int position, CommandContext context) {
        return ArenaPool.instance.getAllArenas().stream()
                .map(arena -> arena.name)
                .collect(Collectors.toSet());
    }
}

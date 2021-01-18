package io.github.llewvallis.chunkwars.command;

import io.github.llewvallis.chunkwars.world.NamedArena;
import io.github.llewvallis.commandbuilder.*;
import org.bukkit.entity.Player;

@AutoCommand
public class JoinArenaCommand extends TopLevelCommand {

    @Override
    public String getName() {
        return "arena";
    }

    @PlayerOnlyCommand
    @ExecuteCommand
    private void execute(CommandContext ctx, NamedArena arena) {
        arena.arena.sendPlayerAndSpectate((Player) ctx.getSender());
    }
}

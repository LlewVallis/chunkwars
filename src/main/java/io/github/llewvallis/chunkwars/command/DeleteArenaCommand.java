package io.github.llewvallis.chunkwars.command;

import io.github.llewvallis.chunkwars.world.ArenaPool;
import io.github.llewvallis.chunkwars.world.NamedArena;
import io.github.llewvallis.commandbuilder.AutoCommand;
import io.github.llewvallis.commandbuilder.CommandContext;
import io.github.llewvallis.commandbuilder.ExecuteCommand;
import io.github.llewvallis.commandbuilder.TopLevelCommand;
import org.bukkit.ChatColor;

@AutoCommand
public class DeleteArenaCommand extends TopLevelCommand {

    @Override
    public String getName() {
        return "arena-delete";
    }

    @ExecuteCommand
    private void execute(CommandContext ctx, NamedArena arena) {
        ArenaPool.instance.delete(arena.name);
        ctx.getSender().sendMessage(ChatColor.GREEN + "Deleted arena " + arena.name);
    }
}

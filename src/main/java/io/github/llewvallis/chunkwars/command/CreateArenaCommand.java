package io.github.llewvallis.chunkwars.command;

import io.github.llewvallis.chunkwars.world.ArenaAlreadyExistsException;
import io.github.llewvallis.chunkwars.world.ArenaPool;
import io.github.llewvallis.commandbuilder.AutoCommand;
import io.github.llewvallis.commandbuilder.CommandContext;
import io.github.llewvallis.commandbuilder.ExecuteCommand;
import io.github.llewvallis.commandbuilder.TopLevelCommand;
import org.bukkit.ChatColor;

@AutoCommand
public class CreateArenaCommand extends TopLevelCommand {

    @Override
    public String getName() {
        return "arena-create";
    }

    @ExecuteCommand
    private void execute(CommandContext ctx, String name) {
        try {
            ArenaPool.instance.create(name);
            ctx.getSender().sendMessage(ChatColor.GREEN + "Provisioned arena " + name);
        } catch (ArenaAlreadyExistsException e) {
            ctx.getSender().sendMessage(ChatColor.RED + "That arena already exists");
        }
    }
}

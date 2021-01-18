package io.github.llewvallis.chunkwars.command;

import io.github.llewvallis.chunkwars.world.Hub;
import io.github.llewvallis.commandbuilder.*;
import org.bukkit.entity.Player;

@AutoCommand
public class HubCommand extends TopLevelCommand {

    @Override
    public String getName() {
        return "hub";
    }

    @PlayerOnlyCommand
    @ExecuteCommand
    private void execute(CommandContext ctx) {
        Hub.instance.sendPlayer((Player) ctx.getSender());
    }
}

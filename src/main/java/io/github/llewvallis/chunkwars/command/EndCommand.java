package io.github.llewvallis.chunkwars.command;

import io.github.llewvallis.chunkwars.team.GameTeam;
import io.github.llewvallis.chunkwars.world.ArenaPool;
import io.github.llewvallis.commandbuilder.*;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@AutoCommand
public class EndCommand extends TopLevelCommand {

    @Override
    public String getName() {
        return "end";
    }

    @PlayerOnlyCommand
    @ExecuteCommand
    private void execute(CommandContext ctx, GameTeam team) {
        Player player = (Player) ctx.getSender();

        ArenaPool.instance.getPlayerArena(player).ifPresentOrElse(
                arena -> {
                    if (arena.arena.isEnded()) {
                        player.sendMessage(ChatColor.RED + "The game has finished");
                    } else {
                        arena.arena.processWinner(team);
                    }
                },
                () -> player.sendMessage(ChatColor.RED + "You are not in an arena")
        );
    }
}

package io.github.llewvallis.chunkwars.handler;

import io.github.llewvallis.chunkwars.world.ArenaPool;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinHandler implements Listener {

    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent e) {
        ArenaPool.instance.get(ArenaPool.DEFAULT_ARENA).ifPresentOrElse(arena -> {
            arena.sendPlayerAndSpectate(e.getPlayer());
        }, () -> {
            e.getPlayer().kickPlayer(ChatColor.RED + "No arena found");
        });
    }
}

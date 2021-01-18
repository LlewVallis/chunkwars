package io.github.llewvallis.chunkwars.handler;

import io.github.llewvallis.chunkwars.team.GameTeam;
import io.github.llewvallis.chunkwars.team.TeamManager;
import io.github.llewvallis.chunkwars.world.ArenaPool;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class BorderTomeHandler implements Listener {

    @EventHandler
    private void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (event.getMaterial() != Material.ENCHANTED_BOOK) {
            return;
        }

        if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_AIR) {
            return;
        }

        player.getInventory().setItem(event.getHand(), null);

        ArenaPool.instance.getPlayerArena(player).ifPresent(arena -> {
            TeamManager.instance.getPlayerTeam(player).ifPresent(team -> {
                if (team == GameTeam.LIGHT) {
                    arena.arena.addDarkBorderBrokenTicks(150);
                }

                if (team == GameTeam.DARK) {
                    arena.arena.addLightBorderBrokenTicks(10 * 20);
                }


                World world = arena.arena.getWorld();
                Location soundLocation = new Location(world, 0, Float.MAX_VALUE / 256, 0);
                world.playSound(soundLocation, Sound.ENTITY_WITHER_SPAWN, SoundCategory.MASTER, Float.MAX_VALUE, 2);
            });
        });
    }
}

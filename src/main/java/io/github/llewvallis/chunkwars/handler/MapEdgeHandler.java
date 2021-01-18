package io.github.llewvallis.chunkwars.handler;

import io.github.llewvallis.chunkwars.team.GameTeam;
import io.github.llewvallis.chunkwars.team.TeamManager;
import io.github.llewvallis.chunkwars.world.ArenaPool;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.concurrent.atomic.AtomicBoolean;

public class MapEdgeHandler implements Listener {

    @EventHandler
    private void onLeaveArena(PlayerMoveEvent e) {
        Player player = e.getPlayer();

        boolean hasWither = player.getActivePotionEffects().stream()
                .map(PotionEffect::getType)
                .anyMatch(type -> type == PotionEffectType.WITHER);

        if (!hasWither && player.getGameMode() == GameMode.SURVIVAL && ArenaPool.instance.inArena(player)) {
            Location location = player.getLocation();

            AtomicBoolean shouldBeKilled = new AtomicBoolean();
            ArenaPool.instance.getPlayerArena(player).ifPresent(arena -> {
                TeamManager.instance.getPlayerTeam(player).ifPresent(team -> {
                    if (team == GameTeam.LIGHT && arena.getArena().getDarkBorderBrokenTicks() == 0 &&
                            location.getZ() < -15.7) {
                        shouldBeKilled.set(true);
                    }

                    if (team == GameTeam.DARK && arena.getArena().getLightBorderBrokenTicks() == 0 &&
                            location.getZ() > 15.7) {
                        shouldBeKilled.set(true);
                    }
                });
            });

            if (shouldBeKilled.get()) {
                player.damage(1000);
            } else if (Math.abs(location.getX()) > 16.3 || Math.abs(location.getZ()) > 32.3) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, Integer.MAX_VALUE, 9));
            }
        }
    }
}

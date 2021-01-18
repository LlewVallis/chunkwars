package io.github.llewvallis.chunkwars.handler;

import io.github.llewvallis.chunkwars.team.GameTeam;
import io.github.llewvallis.chunkwars.team.TeamManager;
import io.github.llewvallis.chunkwars.world.Arena;
import io.github.llewvallis.chunkwars.world.ArenaPool;
import io.github.llewvallis.chunkwars.world.NamedArena;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.Optional;

public class DeathHandler implements Listener {

    @EventHandler
    private void onPlayerDamage(EntityDamageEvent e) {
        Player player;
        if (e.getEntity() instanceof Player) {
            player = (Player) e.getEntity();
        } else {
            return;
        }

        if (player.getGameMode() == GameMode.SPECTATOR) {
            e.setCancelled(true);
            return;
        }

        if (player.getHealth() - e.getFinalDamage() > 0) {
            return;
        }

        Optional<NamedArena> arenaOptional = ArenaPool.instance.getPlayerArena(player);
        Arena arena;
        if (arenaOptional.isPresent()) {
            arena = arenaOptional.get().arena;
        } else {
            return;
        }

        Optional<GameTeam> teamOptional = TeamManager.instance.getPlayerTeam(player);
        GameTeam team;
        if (teamOptional.isPresent()) {
            team = teamOptional.get();
        } else {
            return;
        }

        player.sendTitle(ChatColor.RED + "You died", null, 0, 20, 5);

        World world = player.getWorld();
        Location soundLocation = new Location(world, 0, Float.MAX_VALUE / 256, 0);
        world.playSound(soundLocation, Sound.ENTITY_ENDER_DRAGON_AMBIENT, SoundCategory.MASTER, Float.MAX_VALUE, 1);

        e.setCancelled(true);
        arena.sendPlayerToTeam(player, team, 5);
    }
}

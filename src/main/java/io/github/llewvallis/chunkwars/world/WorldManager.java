package io.github.llewvallis.chunkwars.world;

import io.github.llewvallis.chunkwars.FileUtil;
import io.github.llewvallis.chunkwars.team.TeamManager;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;

public class WorldManager {

    public static WorldManager instance;

    public World getDefaultWorld() {
        return Bukkit.getWorlds().get(0);
    }

    public void cleanArenas() {
        Path worldContainer = Bukkit.getServer().getWorldContainer().toPath();

        try {
            Files.list(worldContainer)
                    .parallel()
                    .filter(Files::isDirectory)
                    .filter(path -> path.getFileName().toString().startsWith("arena-"))
                    .forEach(directory -> {
                        try {
                            Bukkit.getLogger().info("Found dangling arena in directory " + directory);
                            FileUtil.deleteRecursive(directory);
                        } catch (IOException e) {
                            Bukkit.getLogger().log(Level.WARNING, "Failed to delete dangling arena", e);
                        }
                    });
        } catch (IOException e) {
            Bukkit.getLogger().log(Level.WARNING, "Failed to check for dangling arenas", e);
        }
    }

    public void send(Player player, World world, GameMode gameMode) {
        Location spawnPoint = world.getSpawnLocation();
        Location alignedSpawnPoint = new Location(world, spawnPoint.getX() + 0.5, spawnPoint.getY(), spawnPoint.getZ() + 0.5, -90, 0);

        send(player, alignedSpawnPoint, gameMode);
    }

    public void send(Player player, Location location, GameMode gameMode) {
        player.getInventory().clear();
        player.setGameMode(gameMode);
        player.setFlying(false);
        player.setExp(0);
        player.setLevel(0);
        player.setFallDistance(0);
        player.setVelocity(new Vector());
        player.setHealth(20);
        player.setFoodLevel(20);
        player.setSaturation(5);
        player.setBedSpawnLocation(location, true);

        for (PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }

        player.addPotionEffect(new PotionEffect(
                PotionEffectType.NIGHT_VISION,
                Integer.MAX_VALUE,
                0,
                false,
                false,
                false
        ));

        TeamManager.instance.removePlayerFromTeam(player);

        for (NamedArena arena : ArenaPool.instance.getAllArenas()) {
            arena.arena.disownPlayer(player);
        }

        player.teleport(location);

        ArenaPool.instance.getPlayerArena(player).ifPresent(arena -> arena.arena.ownPlayer(player));
    }
}

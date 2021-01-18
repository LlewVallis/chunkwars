package io.github.llewvallis.chunkwars.world;

import com.destroystokyo.paper.Title;
import io.github.llewvallis.chunkwars.ChunkWarsPlugin;
import io.github.llewvallis.chunkwars.FileUtil;
import io.github.llewvallis.chunkwars.handler.ArtifactHandler;
import io.github.llewvallis.chunkwars.handler.WorldBorderHandler;
import io.github.llewvallis.chunkwars.map.ArtifactGenerator;
import io.github.llewvallis.chunkwars.team.GameTeam;
import io.github.llewvallis.chunkwars.team.TeamManager;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;

public class Arena implements AutoCloseable {

    @Getter
    private final World world;
    private final int arenaTickTask;

    @Getter
    private boolean ended = false;
    private boolean closed = false;

    @Getter
    private int lightBorderBrokenTicks = 0;

    private BossBar lightBorderBrokenBossbar;
    private int lightBorderBrokenBossbarSize;

    @Getter
    private int darkBorderBrokenTicks = 0;

    private BossBar darkBorderBrokenBossbar;
    private int darkBorderBrokenBossbarSize;

    @Getter
    private Set<ArtifactHandler.SoldItem> lightBoughtItems = new HashSet<>();
    @Getter
    private Set<ArtifactHandler.SoldItem> darkBoughtItems = new HashSet<>();

    public Arena(World world) {
        this.world = world;

        arenaTickTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(ChunkWarsPlugin.instance,
                () -> {
                    ArtifactGenerator.respawnArtifactsIfNeeded(world);

                    if (lightBorderBrokenTicks > 0) {
                        lightBorderBrokenTicks--;
                        lightBorderBrokenBossbar.setProgress((double) lightBorderBrokenTicks / lightBorderBrokenBossbarSize);

                        if (lightBorderBrokenTicks == 0) {
                            playBorderReinstatedSound();
                            refreshBorder();
                            lightBorderBrokenBossbar.removeAll();
                            lightBorderBrokenBossbar = null;
                        }
                    }

                    if (darkBorderBrokenTicks > 0) {
                        darkBorderBrokenTicks--;
                        darkBorderBrokenBossbar.setProgress((double) darkBorderBrokenTicks / darkBorderBrokenBossbarSize);

                        if (darkBorderBrokenTicks == 0) {
                            playBorderReinstatedSound();
                            refreshBorder();
                            darkBorderBrokenBossbar.removeAll();
                            darkBorderBrokenBossbar = null;
                        }
                    }
                },
                0, 1
        );
    }

    public void disownPlayer(Player player) {
        if (lightBorderBrokenBossbar != null) {
            lightBorderBrokenBossbar.removePlayer(player);
        }

        if (darkBorderBrokenBossbar != null) {
            darkBorderBrokenBossbar.removePlayer(player);
        }
    }

    public void ownPlayer(Player player) {
        if (lightBorderBrokenBossbar != null) {
            lightBorderBrokenBossbar.addPlayer(player);
        }

        if (darkBorderBrokenBossbar != null) {
            darkBorderBrokenBossbar.addPlayer(player);
        }
    }

    public void sendPlayerAndSpectate(Player player) {
        WorldManager.instance.send(player, getRandomSpectatorSpawnLocation(), GameMode.SPECTATOR);
    }

    public void sendPlayerToTeam(Player player, GameTeam team, int respawnTime) {
        TeamManager.instance.addPlayerToTeam(player, team);

        player.setGameMode(GameMode.SPECTATOR);
        player.teleport(getRandomRespawnLocation(team));

        respawnCountdown(player, respawnTime);
    }

    private void respawnCountdown(Player player, int secs) {
        if (secs == 0) {
            if (player.getGameMode() == GameMode.SPECTATOR) {
                ArenaPool.instance.getPlayerArena(player).ifPresent(arena -> {
                    if (arena.arena == this) {
                        TeamManager.instance.getPlayerTeam(player).ifPresent(team -> materializePlayer(player, team));
                    }
                });
            }
        } else {
            ChatColor color = null;
            switch (secs) {
                case 1:
                    color = ChatColor.RED;
                    break;
                case 2:
                    color = ChatColor.GOLD;
                    break;
                case 3:
                    color = ChatColor.YELLOW;
                    break;
            }

            if (color != null) {
                player.sendTitle(color.toString() + secs, null, 0, 20, 0);
            }

            Bukkit.getScheduler().scheduleSyncDelayedTask(ChunkWarsPlugin.instance,
                    () -> respawnCountdown(player, secs -1), 20);
        }
    }

    private void materializePlayer(Player player, GameTeam team) {
        Location location = player.getLocation();
        player.getWorld().playSound(location, Sound.BLOCK_BEACON_POWER_SELECT, SoundCategory.MASTER, 1, 1);

        double minZ = team == GameTeam.LIGHT ? 16.5 : -31.5;
        double maxZ = team == GameTeam.LIGHT ? 31.5 : -16.5;

        double x = location.getX();
        double z = location.getZ();
        x = Math.max(-15.5, Math.min(15.5, x));
        z = Math.max(minZ, Math.min(maxZ, z));
        location.setX(x);
        location.setZ(z);

        double y = highestBlockInLineWith(player, location) + 1;
        location.setY(y);

        WorldManager.instance.send(player, location, GameMode.SURVIVAL);
        TeamManager.instance.addPlayerToTeam(player, team);
    }

    private double highestBlockInLineWith(Player player, Location location) {
        double widthX = player.getBoundingBox().getWidthX();
        int startX = location.clone().subtract(widthX / 2, 0, 0).getBlockX();
        int endX = location.clone().add(widthX / 2, 0, 0).getBlockX();

        double widthZ = player.getBoundingBox().getWidthZ();
        int startZ = location.clone().subtract(widthZ / 2, 0, 0).getBlockZ();
        int endZ = location.clone().add(widthZ / 2, 0, 0).getBlockZ();

        int y = 0;

        for (int x = startX; x <= endX; x++) {
            for (int z = startZ; z <= endZ; z++) {
                y = Math.max(y, player.getWorld().getHighestBlockYAt(x, z));
            }
        }

        return y == 0 ? location.getY() : y;
    }

    public boolean isAttachedToWorld(World world) {
        return world.equals(this.world);
    }

    @Override
    public void close() {
        if (closed) {
            return;
        }

        closed = true;

        Bukkit.getScheduler().cancelTask(arenaTickTask);

        for (Player player : world.getPlayers()) {
            Hub.instance.sendPlayer(player);
        }

        Bukkit.getLogger().info("Unloading arena " + world.getName());
        boolean success = Bukkit.unloadWorld(world, false);

        if (!success) {
            Bukkit.getLogger().warning("Could not unload " + world.getName());
            return;
        }

        File worldDirectory = world.getWorldFolder();

        if (worldDirectory.exists()) {
            try {
                FileUtil.deleteRecursive(worldDirectory.toPath());
            } catch (IOException e) {
                Bukkit.getLogger().log(Level.WARNING, "Failed to clean world files for " + world.getName(), e);
            }
        } else {
            Bukkit.getLogger().warning("Could not clean world files for " + world.getName() + " as they did not exist");
        }
    }

    public boolean processWinner(GameTeam team) {
        if (ended) {
            return false;
        }

        if (lightBorderBrokenBossbar != null) {
            lightBorderBrokenBossbar.removeAll();
            lightBorderBrokenBossbar = null;
        }

        if (darkBorderBrokenBossbar != null) {
            darkBorderBrokenBossbar.removeAll();
            darkBorderBrokenBossbar = null;
        }

        String teamName = team == GameTeam.LIGHT ? "Light" : "Dark";
        ChatColor color = team == GameTeam.LIGHT ? ChatColor.YELLOW : ChatColor.DARK_PURPLE;

        TextComponent titleMessage = new TextComponent(teamName + " wins the game");
        titleMessage.setColor(color);
        Title title = new Title(titleMessage, null, 5, 80, 40);

        int crystalZLocation = team == GameTeam.LIGHT ? ArtifactGenerator.DARK_Z : ArtifactGenerator.LIGHT_Z;
        EnderCrystal crystal = world.getEntitiesByClass(EnderCrystal.class).stream()
                .filter(entity -> entity.getLocation().getX() == 0 && entity.getLocation().getZ() == crystalZLocation)
                .findAny()
                .orElseThrow();

        for (Player player : world.getPlayers()) {
            player.sendTitle(title);

            double direction = Math.PI * 2 * Math.random();
            Location spectatorLocation = crystal.getLocation().add(
                    Math.cos(direction) * 17.5,
                    12.5,
                    Math.sin(direction) * 17.5
            );

            spectatorLocation.setYaw((float) Math.toDegrees(direction) + 90);
            spectatorLocation.setPitch(45);

            WorldManager.instance.send(player, spectatorLocation, GameMode.SPECTATOR);
        }

        crystal.remove();

        for (int x = -1; x <= 0; x++) {
            for (int z = crystalZLocation - 1; z <= crystalZLocation; z++) {
                for (int y = 0; y < 256; y++) {
                    Block block = world.getBlockAt(x, y, z);
                    if (block.getType() == Material.NETHERITE_BLOCK) {
                        block.setType(Material.POLISHED_ANDESITE);
                    }
                }
            }
        }

        for (int i = 0; i < 10; i++) {
            Location explosionLocation = crystal.getLocation().add(
                    new Random().nextGaussian() * i / 2,
                    new Random().nextGaussian() * i / 2,
                    new Random().nextGaussian() * i / 2
            );

            Bukkit.getScheduler().scheduleSyncDelayedTask(ChunkWarsPlugin.instance, () -> {
                world.createExplosion(explosionLocation, 6, true);
            }, i * 2);
        }

        Location soundLocation = new Location(world, 0, Float.MAX_VALUE / 256, 0);
        world.playSound(soundLocation, Sound.ENTITY_WITHER_DEATH, SoundCategory.MASTER, Float.MAX_VALUE, 1);

        Bukkit.getScheduler().cancelTask(arenaTickTask);

        Bukkit.getScheduler().scheduleSyncDelayedTask(ChunkWarsPlugin.instance, () -> {
            if (!closed) {
                ArenaPool.instance.reset(this);
            }
        }, 5 * 20);

        ended = true;
        return true;
    }

    public void addLightBorderBrokenTicks(int ticks) {
        lightBorderBrokenTicks += ticks;
        refreshBorder();

        if (lightBorderBrokenBossbar != null) {
            lightBorderBrokenBossbar.removeAll();
        }

        lightBorderBrokenBossbarSize = lightBorderBrokenTicks;
        lightBorderBrokenBossbar = Bukkit.createBossBar(
                "Light Border Broken", BarColor.RED, BarStyle.SOLID, BarFlag.CREATE_FOG, BarFlag.DARKEN_SKY
        );

        for (Player player : world.getPlayers()) {
            lightBorderBrokenBossbar.addPlayer(player);
        }
    }

    public void addDarkBorderBrokenTicks(int ticks) {
        darkBorderBrokenTicks += ticks;
        refreshBorder();

        if (darkBorderBrokenBossbar != null) {
            darkBorderBrokenBossbar.removeAll();
        }

        darkBorderBrokenBossbarSize = darkBorderBrokenTicks;
        darkBorderBrokenBossbar = Bukkit.createBossBar(
                "Dark Border Broken", BarColor.RED, BarStyle.SOLID, BarFlag.CREATE_FOG, BarFlag.DARKEN_SKY
        );

        for (Player player : world.getPlayers()) {
            darkBorderBrokenBossbar.addPlayer(player);
        }
    }

    private void refreshBorder() {
        for (Player player : world.getPlayers()) {
            WorldBorderHandler.instance.refreshBorder(player);
        }
    }

    private void playBorderReinstatedSound() {
        Location soundLocation = new Location(world, 0, Float.MAX_VALUE / 256, 0);
        world.playSound(soundLocation, Sound.ENTITY_ELDER_GUARDIAN_CURSE, SoundCategory.MASTER, Float.MAX_VALUE, 0.75f);
    }

    public Location getRandomSpectatorSpawnLocation() {
        double direction = Math.random() * Math.PI * 2;

        Vector location = new Vector(25, 130, 0);
        location.rotateAroundY(direction);

        return location.toLocation(world, (float) Math.toDegrees(-direction) + 90, 35);
    }

    private Location getRandomRespawnLocation(GameTeam team) {
        double direction = team == GameTeam.LIGHT ? Math.PI * 3 / 2 : Math.PI / 2;
        direction += (Math.random() - 0.5) / 4 * Math.PI;

        Vector location = new Vector(31, 130, 0);
        location.rotateAroundY(direction);

        return location.toLocation(world, (float) Math.toDegrees(-direction) + 90, 60);
    }
}

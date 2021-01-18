package io.github.llewvallis.chunkwars.handler;

import io.github.llewvallis.chunkwars.ChunkWarsPlugin;
import io.github.llewvallis.chunkwars.team.GameTeam;
import io.github.llewvallis.chunkwars.team.TeamManager;
import io.github.llewvallis.chunkwars.world.ArenaPool;
import io.github.llewvallis.chunkwars.world.NamedArena;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

import java.util.Random;

public class BorderOutlineHandler {

    private static final double SCALE_X = 0.66;
    private static final double SCALE_Y = 0.66;

    private final Random random = new Random();

    public BorderOutlineHandler() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(ChunkWarsPlugin.instance, this::update, 0, 2);
    }

    private void update() {
        ArenaPool.instance.getAllArenas().stream()
                .flatMap(arena -> arena.arena.getWorld().getPlayers().stream())
                .filter(ArenaPool.instance::inArena)
                .forEach(this::updatePlayer);
    }

    private void updatePlayer(Player player, int wallZ, int baseRadius, Particle.DustOptions dust) {
        double playerZ = player.getLocation().getZ();
        double distance = Math.abs(playerZ - wallZ);

        double radius = Math.sqrt(baseRadius * baseRadius - distance * distance);
        if (Double.isNaN(radius)) {
            return;
        }

        double baseX = player.getLocation().getX();
        double baseY = player.getLocation().getY() + 2;

        double maxX = radius * SCALE_X;

        for (double x = -maxX; x <= maxX; x += 0.25) {
            double unscaledX = x / SCALE_X;
            double maxY = Math.sqrt(radius * radius - unscaledX * unscaledX) * SCALE_Y;

            for (double y = -maxY; y <= maxY; y += 0.25) {
                if (random.nextInt(24) != 0) {
                    continue;
                }

                double fullX = x + baseX + random.nextGaussian() / 5;
                double fullY = y + baseY + random.nextGaussian() / 5;

                player.spawnParticle(Particle.REDSTONE, fullX, fullY, wallZ, 1, 0, 0, 0, 0, dust);
            }
        }
    }

    private void updatePlayer(Player player) {
        GameTeam team = TeamManager.instance.getPlayerTeam(player).orElseThrow();
        NamedArena arena = ArenaPool.instance.getPlayerArena(player).orElseThrow();

        Particle.DustOptions blueDust = new Particle.DustOptions(Color.fromRGB(153, 255, 255), 1);
        Particle.DustOptions redDust = new Particle.DustOptions(Color.fromRGB(255, 0, 0), 1.5f);

        if (team == GameTeam.LIGHT) {
            if (arena.arena.getLightBorderBrokenTicks() > 0) {
                updatePlayer(player, 16, 8, redDust);
            } else {
                updatePlayer(player, 16, 4, blueDust);
            }

            if (arena.arena.getDarkBorderBrokenTicks() > 0) {
                updatePlayer(player, -16, 8, redDust);
            }
        }

        if (team == GameTeam.DARK) {
            if (arena.arena.getDarkBorderBrokenTicks() > 0) {
                updatePlayer(player, -16, 8, redDust);
            } else {
                updatePlayer(player, -16, 4, blueDust);
            }

            if (arena.arena.getLightBorderBrokenTicks() > 0) {
                updatePlayer(player, 16, 8, redDust);
            }
        }
    }
}

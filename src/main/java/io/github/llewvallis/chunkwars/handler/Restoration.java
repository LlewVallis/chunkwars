package io.github.llewvallis.chunkwars.handler;

import io.github.llewvallis.chunkwars.ChunkWarsPlugin;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.PistonMoveReaction;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.util.Vector;

import java.util.List;

public class Restoration implements Listener {

    public static final String RESTORATION_META_KEY = "chunkwars-restores-to";

    private static final int RADIUS = 5;
    private static final int PULSE_DELAY = 3 * 20;

    private static final int[][] PARTICLE_EDGES = {
            // CUBE EDGES
            { 1, 1, 1, -1, 1, 1 },
            { 1, 1, 1, 1, 1, -1 },
            { -1, 1, 1, -1, 1, -1 },
            { 1, 1, -1, -1, 1, -1 },
            { 1, -1, 1, -1, -1, 1 },
            { 1, -1, 1, 1, -1, -1 },
            { -1, -1, 1, -1, -1, -1 },
            { 1, -1, -1, -1, -1, -1 },
            { 1, 1, 1, 1, -1, 1 },
            { -1, 1, 1, -1, -1, 1 },
            { 1, 1, -1, 1, -1, -1 },
            { -1, 1, -1, -1, -1, -1 },

            // X DIAGONALS
            { 1, 1, -1, 1, -1, 1 },
            { 1, 1, 1, 1, -1, -1 },
            { -1, 1, -1, -1, -1, 1 },
            { -1, 1, 1, -1, -1, -1 },

            // Y DIAGONALS
            { 1, 1, 1, -1, 1, -1 },
            { -1, 1, 1, 1, 1, -1 },
            { 1, -1, 1, -1, -1, -1 },
            { -1, -1, 1, 1, -1, -1 },

            // Z DIAGONALS
            { 1, -1, 1, -1, 1, 1 },
            { 1, 1, 1, -1, -1, 1 },
            { 1, -1, -1, -1, 1, -1 },
            { 1, 1, -1, -1, -1, -1 },

            // SPOKES
            { 0, 0, 0, 1, 1, 1 },
            { 0, 0, 0, -1, 1, 1 },
            { 0, 0, 0, 1, -1, 1 },
            { 0, 0, 0, -1, -1, 1 },
            { 0, 0, 0, 1, 1, -1 },
            { 0, 0, 0, -1, 1, -1 },
            { 0, 0, 0, 1, -1, -1 },
            { 0, 0, 0, -1, -1, -1 },
    };

    @EventHandler
    private void onRestorationKitPlace(BlockPlaceEvent e) {
        if (e.getBlock().getType() != Material.CONDUIT) {
            return;
        }

        pulseRestorationKit(e.getBlock().getLocation(), 1);
    }

    @EventHandler
    private void onBlockFall(EntityChangeBlockEvent e) {
        if (!(e.getEntity() instanceof FallingBlock)) {
            return;
        }

        if (e.getTo() != Material.AIR) {
            return;
        }

        for (MetadataValue metadataValue : e.getBlock().getMetadata(RESTORATION_META_KEY)) {
            e.getEntity().setMetadata(RESTORATION_META_KEY, metadataValue);
        }
    }

    @EventHandler
    private void onPistonPush(BlockPistonExtendEvent e) {
        onPistonMove(e.getBlocks(), e.getDirection());
    }

    @EventHandler
    private void onPistonPull(BlockPistonRetractEvent e) {
        onPistonMove(e.getBlocks(), e.getDirection());
    }

    private void onPistonMove(List<Block> blocks, BlockFace direction) {
        for (Block block : blocks) {
            Location destination = block.getLocation().add(direction.getDirection());
            List<MetadataValue> metadataValues = block.getMetadata(RESTORATION_META_KEY);
            block.removeMetadata(RESTORATION_META_KEY, ChunkWarsPlugin.instance);

            for (MetadataValue metadataValue : metadataValues) {
                Bukkit.getScheduler().scheduleSyncDelayedTask(ChunkWarsPlugin.instance, () -> {
                    destination.getBlock().setMetadata(RESTORATION_META_KEY, metadataValue);
                }, 3);
            }
        }
    }

    @EventHandler
    private void onBlockChange(BlockPhysicsEvent e) {
        if (e.getSourceBlock() != e.getBlock()) {
            return;
        }

        e.getBlock().removeMetadata(RESTORATION_META_KEY, ChunkWarsPlugin.instance);
    }

    @EventHandler
    private void onBlockLand(EntityChangeBlockEvent e) {
        if (!(e.getEntity() instanceof FallingBlock)) {
            return;
        }

        if (e.getTo() == Material.AIR) {
            return;
        }

        List<MetadataValue> metadataValues = e.getEntity().getMetadata(RESTORATION_META_KEY);

        Bukkit.getScheduler().runTaskLater(ChunkWarsPlugin.instance, () -> {
            for (MetadataValue metadataValue : metadataValues) {
                e.getBlock().setMetadata(RESTORATION_META_KEY, metadataValue);
            }
        }, 0);
    }

    private void pulseRestorationKit(Location location, int radius) {
        for (int deltaX = -radius; deltaX <= radius; deltaX++) {
            for (int deltaY = -radius; deltaY <= radius; deltaY++) {
                for (int deltaZ = -radius; deltaZ <= radius; deltaZ++) {
                    if (Math.abs(deltaX) != radius && Math.abs(deltaY) != radius && Math.abs(deltaZ) != radius) {
                        continue;
                    }

                    Block restoreBlock = location.clone().add(deltaX, deltaY, deltaZ).getBlock();
                    List<MetadataValue> metadataValues = restoreBlock.getMetadata(RESTORATION_META_KEY);

                    if (metadataValues.size() != 1) {
                        if (metadataValues.size() != 0) {
                            ChunkWarsPlugin.instance.getLogger().warning("Multiple metadata keys found when restoring block");
                        }

                        continue;
                    }

                    String restorationNbt = metadataValues.get(0).asString();
                    BlockData newBlockData = Bukkit.createBlockData(restorationNbt);
                    restoreBlock.setBlockData(newBlockData);

                    boolean requiresAirAbove = restoreBlock.getType() == Material.FARMLAND ||
                            restoreBlock.getType() == Material.GRASS_PATH;

                    if (requiresAirAbove && !restoreBlock.getRelative(BlockFace.UP).isEmpty()) {
                        restoreBlock.setType(Material.DIRT);
                    }
                }
            }
        }

        Location particleLocation = location.toCenterLocation();

        for (int[] edge : PARTICLE_EDGES) {
            drawParticles(
                    Particle.VILLAGER_HAPPY,
                    particleLocation,
                    edge[0] * radius,
                    edge[1] * radius,
                    edge[2] * radius,
                    edge[3] * radius,
                    edge[4] * radius,
                    edge[5] * radius
            );
        }

        if (radius < RADIUS) {
            location.getWorld().playSound(location, Sound.BLOCK_ANVIL_USE, SoundCategory.MASTER, 1, 1);

            BlockCallbackHandler.instance.register(location, PULSE_DELAY, () -> {
                pulseRestorationKit(location, radius + 1);
            }, () -> {});
        } else {
            location.getWorld().playSound(location, Sound.BLOCK_ANVIL_DESTROY, SoundCategory.MASTER, 1, 1);

            BlockCallbackHandler.instance.register(location, 15, () -> {
                location.getBlock().setType(Material.AIR);
                location.getWorld().spawnParticle(Particle.FLASH, location.toCenterLocation(), 0);
            }, () -> {});
        }
    }

    private void drawParticles(Particle particle, Location origin, double startX, double startY, double startZ, double endX, double endY, double endZ) {
        Vector start = origin.toVector().add(new Vector(startX, startY, startZ));
        Vector end = origin.toVector().add(new Vector(endX, endY, endZ));

        Vector line = end.clone().subtract(start);
        double stepSize = line.length() / Math.ceil(line.length()) / 4 - 0.001;
        Vector step = line.clone().normalize().multiply(stepSize);

        int stepCount = (int) Math.ceil(line.length() / stepSize);

        Vector point = start.clone();
        for (int i = 0; i < stepCount; i++) {
            origin.getWorld().spawnParticle(particle, point.toLocation(origin.getWorld()), 0);
            point.add(step);
        }
    }
}

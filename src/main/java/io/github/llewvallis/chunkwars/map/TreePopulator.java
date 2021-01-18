package io.github.llewvallis.chunkwars.map;

import io.github.llewvallis.chunkwars.ChunkWarsPlugin;
import io.github.llewvallis.chunkwars.team.GameTeam;
import org.bukkit.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.stream.Collectors;

public class TreePopulator extends Populator {

    public TreePopulator(int seed) {
        super(seed);
    }

    @Override
    protected void populate() {
        if (chunk.getX() != 0 || (chunk.getZ() != -2 && chunk.getZ() != 1)) {
            return;
        }

        double x = random.nextInt(8) + 4 + random.nextDouble() / 2 - 0.25;
        double z = random.nextInt(8) + 4 + random.nextDouble() / 2 - 0.25;
        int y = getTerrainHeight(x, z);

        generateTree(x, y, z);
    }

    public static void spawnTree(Location location, GameTeam team) {
        int seed = new Random().nextInt();

        TreePopulator populator = new TreePopulator(seed);

        populator.sideSwapProtection = false;
        populator.world = location.getWorld();
        populator.chunk = location.getChunk();
        populator.random = new Random(seed);
        populator.team = team;
        populator.changes = new HashMap<>();

        int x = Math.floorMod(location.getBlockX(), 16);
        int z = Math.floorMod(location.getBlockZ(), 16);
        if (team == GameTeam.DARK) {
            z = 15 - z;
        }

        Location localLocation = new Location(location.getWorld(), x, location.getBlockY(), z);
        populator.generateTree(localLocation.getX(), localLocation.getBlockY(), localLocation.getZ());

        List<Map.Entry<Location, BlockData>> changes = populator.changes.entrySet().stream()
                .collect(Collectors.collectingAndThen(
                        Collectors.toList(),
                        list -> {
                            Collections.shuffle(list);
                            return list;
                        }
                )).stream()
                .sorted(Comparator.comparingDouble(entry -> entry.getKey().distance(location)))
                .sorted(Comparator.comparingDouble(entry -> Math.abs(entry.getKey().getY() - localLocation.getY())))
                .sorted(Comparator.comparingInt(entry -> {
                    Material material = entry.getValue().getMaterial();
                    return Tag.LOGS.isTagged(material) ? 0 : 1;
                }))
                .collect(Collectors.toList());

        Location particleLocation = location.clone();
        particleLocation.add(0, 1, 0);
        particleLocation.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, particleLocation, 20, 0.25, 0.25, 0.25);

        location.getWorld().playSound(location, Sound.BLOCK_BEEHIVE_EXIT, SoundCategory.MASTER, 1, 1.3f);

        Bukkit.getScheduler().scheduleSyncDelayedTask(ChunkWarsPlugin.instance, () -> applyChanges(changes), 10);
    }

    private static void applyChanges(List<Map.Entry<Location, BlockData>> changes) {
        if (changes.isEmpty()) {
            return;
        }

        Map.Entry<Location, BlockData> change = changes.remove(0);
        Location location = change.getKey();

        Location centeredLocation = location.clone();
        centeredLocation.add(0.5, 0.5, 0.5);

        boolean isLog = Tag.LOGS.isTagged(change.getValue().getMaterial());

        if (changes.size() % 2 == 0) {
            Sound sound = isLog ? Sound.BLOCK_WOOD_PLACE : Sound.BLOCK_GRASS_PLACE;
            centeredLocation.getWorld().playSound(centeredLocation, sound, SoundCategory.MASTER, 1, 1);
        }

        double knockbackFactor = isLog ? 1.5 : 0.5;
        double teleportDistance = isLog ? 4 : 2;

        for (Entity entity : location.getWorld().getNearbyEntities(centeredLocation, 0.5, 0.5, 0.5)) {
            Vector velocity = entity.getVelocity();

            velocity.add(new Vector(0, 2, 0));
            velocity.normalize();
            velocity.multiply(knockbackFactor);

            entity.setVelocity(velocity);

            Location entityLocation = entity.getLocation();
            entityLocation.setY(teleportDistance + location.getBlockY());
            entity.teleport(entityLocation);

            if (entity instanceof LivingEntity && isLog) {
                ((LivingEntity) entity).addPotionEffect(new PotionEffect(
                        PotionEffectType.JUMP, 50, 255, false, false, false
                ));
            }
        }

        location.getBlock().setBlockData(change.getValue());
        Bukkit.getScheduler().scheduleSyncDelayedTask(ChunkWarsPlugin.instance, () -> applyChanges(changes), 1);
    }

    private void generateTree(double x, int y, double z) {
        int height = y + random.nextInt(2) + 9;
        double direction = random.nextDouble() * Math.PI * 2;

        for (int i = 0; i < 3; i++) {
            double newX = x + random.nextDouble() * 1.5 - 0.75;
            double newZ = z + random.nextDouble() * 1.5 - 0.75;
            generateLog(newX, y, newZ);
        }

        for (; y < height; y++) {
            generateLog(x, y, z);

            direction += random.nextDouble() / 10;

            x += Math.cos(direction) / 6;
            z += Math.sin(direction) / 6;
        }

        generateLeaves(x, y, z, direction);
    }

    private void generateLog(double x, int y, double z) {
        setBlock(x, y, z, logMaterial());
        setBlock(x, y - 1, z, logMaterial());

        Material groundMaterial = getBlock(x, y - 2, z) ;
        if (groundMaterial == Material.GRASS_BLOCK || groundMaterial == Material.MYCELIUM || groundMaterial == Material.PODZOL) {
            setBlock(x, y - 2, z, Material.DIRT);
        }
    }

    private void generateLeaves(double baseX, int baseY, double baseZ, double direction) {
        for (double x = baseX - 3; x <= baseX + 3; x++) {
            for (double z = baseZ - 3; z <= baseZ + 3; z++) {
                for (int y = baseY - 3; y <= baseY + 3; y++) {
                    int deltaY = y - baseY;

                    double deltaX = x - baseX - Math.cos(direction) * deltaY / 8;
                    double deltaZ = z - baseZ - Math.sin(direction) * deltaY / 8;

                    double distanceSquared = deltaX * deltaX + deltaZ * deltaZ;
                    double cutoffDistance = 4 - deltaY;

                    if (distanceSquared > cutoffDistance) {
                        continue;
                    }

                    if (getBlock(x, y, z) != Material.AIR) {
                        continue;
                    }

                    setBlock(x, y, z, leavesMaterial());

                    if (random.nextInt((int) distanceSquared + 1) != 0) {
                        continue;
                    }

                    if (getBlock(x, y - 1, z) != Material.AIR) {
                        continue;
                    }

                    setBlock(x, y - 1, z, leavesMaterial());
                }
            }
        }
    }

    private Material logMaterial() {
        if (team == GameTeam.DARK) {
            return profile.darkWood();
        } else {
            return profile.lightWood();
        }
    }

    private Material leavesMaterial() {
        if (team == GameTeam.DARK) {
            return profile.darkLeaves();
        } else {
            return profile.lightLeaves();
        }
    }
}

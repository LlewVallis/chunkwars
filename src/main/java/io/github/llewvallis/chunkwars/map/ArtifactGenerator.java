package io.github.llewvallis.chunkwars.map;

import lombok.experimental.UtilityClass;
import org.bukkit.HeightMap;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.EntityType;

@UtilityClass
public class ArtifactGenerator {

    public static final int LIGHT_Z = 26;
    public static final int DARK_Z = -26;

    public void generate(World world) {
        generate(world, LIGHT_Z);
        generate(world, DARK_Z);
    }

    private void generate(World world, int targetZ) {
        int top = 0;

        for (int x = -1; x <= 0; x++) {
            for (int z = targetZ - 1; z <= targetZ; z++) {
                top = Math.max(top, world.getHighestBlockYAt(x, z, HeightMap.MOTION_BLOCKING_NO_LEAVES));
            }
        }

        top += 1;

        for (int x = -1; x <= 0; x++) {
            for (int z = targetZ - 1; z <= targetZ; z++) {
                boolean underground = false;

                for (int y = top; y >= 0; y--) {
                    Block block = world.getBlockAt(x, y, z);

                    if (block.getType() != Material.AIR) {
                        block.setType(Material.NETHERITE_BLOCK);
                        underground = true;
                    } else if (!underground) {
                        block.setType(Material.NETHERITE_BLOCK);
                    }
                }
            }
        }

        generateCrystal(world, targetZ);
    }

    public void respawnArtifactsIfNeeded(World world) {
        boolean light = world.getEntitiesByClass(EnderCrystal.class).stream()
                .anyMatch(entity -> entity.getLocation().getX() == 0 && entity.getLocation().getZ() == LIGHT_Z);

        if (!light) {
            ArtifactGenerator.generateCrystal(world, LIGHT_Z);
        }

        boolean dark = world.getEntitiesByClass(EnderCrystal.class).stream()
                .anyMatch(entity -> entity.getLocation().getX() == 0 && entity.getLocation().getZ() == DARK_Z);

        if (!dark) {
            ArtifactGenerator.generateCrystal(world, DARK_Z);
        }
    }

    private void generateCrystal(World world, int z) {
        int y = world.getHighestBlockYAt(0, z, HeightMap.MOTION_BLOCKING_NO_LEAVES);
        world.spawnEntity(new Location(world, 0, y + 1.33, z), EntityType.ENDER_CRYSTAL);
    }
}

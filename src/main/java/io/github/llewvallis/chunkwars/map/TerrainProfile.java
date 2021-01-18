package io.github.llewvallis.chunkwars.map;

import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.block.Biome;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@AllArgsConstructor
public class TerrainProfile {

    private final int seed;

    private final Random transientRandom = new Random();

    private final int LIGHT_WOOD = transientRandom.nextInt();
    private final int DARK_WOOD = transientRandom.nextInt();
    private final int LIGHT_LEAVES = transientRandom.nextInt();
    private final int DARK_LEAVES = transientRandom.nextInt();

    private final int TERRAIN_SCALE = transientRandom.nextInt();
    private final int TERRAIN_FREQUENCY_X = transientRandom.nextInt();
    private final int TERRAIN_FREQUENCY_Z = transientRandom.nextInt();
    private final int BIOME = transientRandom.nextInt();

    private final int FOLIAGE_DENSITY = transientRandom.nextInt();
    private final int FLOWER = transientRandom.nextInt();
    private final int MUSHROOM = transientRandom.nextInt();

    private final int SCORCHED_FOLIAGE_DENSITY = transientRandom.nextInt();

    private final int DIRT = transientRandom.nextInt();
    private final int STONE = transientRandom.nextInt();

    private final int BASALT = transientRandom.nextInt();
    private final int BLACKSTONE = transientRandom.nextInt();
    private final int NETHER_WOOD = transientRandom.nextInt();

    private final int DIRT_SPREAD = transientRandom.nextInt();

    private Random random(int id) {
        return new Random(seed + id);
    }

    private <T> T choice(int id, List<T> items) {
        Random random = random(id);
        int extremism = random.nextInt(5);

        List<T> choices = new ArrayList<>(items);
        for (int i = 0; i < items.size() / 2; i++) {
            T choice = choices.get(random.nextInt(choices.size()));
            for (int j = 0; j < (i + 1) * extremism; j++) {
                choices.add(choice);
            }
        }

        int index = transientRandom.nextInt(choices.size());
        return choices.get(index);
    }

    public Material lightWood() {
        int type = random(LIGHT_WOOD).nextInt(3);

        if (type == 0) {
            return Material.BIRCH_WOOD;
        } else if (type == 1) {
            return Material.ACACIA_WOOD;
        } else {
            return Material.OAK_WOOD;
        }
    }

    public Material darkWood() {
        int type = random(DARK_WOOD).nextInt(3);

        if (type == 0) {
            return Material.DARK_OAK_WOOD;
        } else if (type == 1) {
            return Material.SPRUCE_WOOD;
        } else {
            return Material.JUNGLE_WOOD;
        }
    }

    public Material lightLeaves() {
        return choice(LIGHT_LEAVES, List.of(
                Material.OAK_LEAVES,
                Material.ACACIA_LEAVES,
                Material.JUNGLE_LEAVES
        ));
    }

    public Material darkLeaves() {
        return choice(DARK_LEAVES, List.of(
                Material.SPRUCE_LEAVES,
                Material.BIRCH_LEAVES
        ));
    }

    public double terrainScale() {
        return 80 + (random(TERRAIN_SCALE).nextDouble() - 0.5) * 12;
    }

    public double terrainFrequencyX() {
        return 0.025 + (random(TERRAIN_FREQUENCY_X).nextDouble()) * 0.02;
    }

    public double terrainFrequencyZ() {
        return 0.015 + (random(TERRAIN_FREQUENCY_Z).nextDouble()) * 0.02;
    }

    public Biome biome() {
        return random(BIOME).nextBoolean() ? Biome.PLAINS : Biome.SAVANNA;
    }

    public boolean shouldApplyFoliage() {
        double chance = random(FOLIAGE_DENSITY).nextDouble() / 8;
        return transientRandom.nextDouble() < chance;
    }

    public boolean shouldApplyScorchedFoliage() {
        double chance = random(SCORCHED_FOLIAGE_DENSITY).nextDouble();
        return transientRandom.nextDouble() < chance;
    }

    public boolean shouldAddCrimsonRoots() {
        double chance = random(SCORCHED_FOLIAGE_DENSITY).nextDouble() / 2;
        return transientRandom.nextDouble() < chance;
    }

    public Material flower() {
        return choice(FLOWER, List.of(
                Material.CORNFLOWER,
                Material.BLUE_ORCHID,
                Material.ALLIUM
        ));
    }

    public Material mushroom() {
        return choice(MUSHROOM, List.of(
                Material.RED_MUSHROOM,
                Material.BROWN_MUSHROOM
        ));
    }

    public Material dirt() {
        return choice(DIRT, List.of(
                Material.DIRT,
                Material.DIRT,
                Material.DIRT,
                Material.COARSE_DIRT,
                Material.COARSE_DIRT,
                Material.COARSE_DIRT,
                Material.PODZOL,
                Material.PODZOL
        ));
    }

    public Material stone() {
        return choice(STONE, List.of(
                Material.STONE,
                Material.STONE,
                Material.STONE,
                Material.ANDESITE,
                Material.ANDESITE,
                Material.COBBLESTONE
        ));
    }

    public Material basalt() {
        return choice(BASALT, List.of(
                Material.BASALT,
                Material.BASALT,
                Material.POLISHED_BASALT
        ));
    }

    public Material blackstone() {
        return choice(BLACKSTONE, List.of(
                Material.POLISHED_BLACKSTONE_BRICKS,
                Material.POLISHED_BLACKSTONE_BRICKS,
                Material.POLISHED_BLACKSTONE_BRICKS,
                Material.POLISHED_BLACKSTONE,
                Material.POLISHED_BLACKSTONE
        ));
    }

    public Material netherWood() {
        return choice(NETHER_WOOD, List.of(
                Material.CRIMSON_HYPHAE,
                Material.WARPED_HYPHAE
        ));
    }

    public double dirtSpreadFactor() {
        return random(DIRT_SPREAD).nextDouble() * 0.5 + 0.25;
    }
}

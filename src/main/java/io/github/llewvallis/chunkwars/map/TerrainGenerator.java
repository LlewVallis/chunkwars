package io.github.llewvallis.chunkwars.map;

import lombok.Value;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.generator.ChunkGenerator;
import org.spongepowered.noise.Noise;
import org.spongepowered.noise.NoiseQuality;

import java.util.List;
import java.util.Random;
import java.util.Set;

public class TerrainGenerator extends ChunkGenerator {

    private static final int HEIGHT_OFFSET = 90;

    private static final Set<ChunkCoord> ARENA_COORDS = Set.of(
            new ChunkCoord(0, 0),
            new ChunkCoord(-1, 0),
            new ChunkCoord(0, -1),
            new ChunkCoord(-1, -1),
            new ChunkCoord(0, 1),
            new ChunkCoord(-1, 1),
            new ChunkCoord(0, -2),
            new ChunkCoord(-1, -2)
    );

    private static final Set<ChunkCoord> BARRIER_COORDS = Set.of(
            new ChunkCoord(-3, -4),
            new ChunkCoord(-3, -3),
            new ChunkCoord(-3, -2),
            new ChunkCoord(-3, -1),
            new ChunkCoord(-3, 0),
            new ChunkCoord(-3, 1),
            new ChunkCoord(-3, 2),
            new ChunkCoord(-3, 3),
            new ChunkCoord(2, -4),
            new ChunkCoord(2, -3),
            new ChunkCoord(2, -2),
            new ChunkCoord(2, -1),
            new ChunkCoord(2, 0),
            new ChunkCoord(2, 1),
            new ChunkCoord(2, 2),
            new ChunkCoord(2, 3),
            new ChunkCoord(-2, -4),
            new ChunkCoord(-1, -4),
            new ChunkCoord(0, -4),
            new ChunkCoord(1, -4),
            new ChunkCoord(-2, 3),
            new ChunkCoord(-1, 3),
            new ChunkCoord(0, 3),
            new ChunkCoord(1, 3)
    );

    private final int seed;

    private final TerrainProfile profile;

    private final double terrainScale;
    private final double terrainFrequencyX;
    private final double terrainFrequencyZ;

    public TerrainGenerator(int seed) {
        this.seed = seed;

        profile = new TerrainProfile(seed);
        terrainScale = profile.terrainScale();
        terrainFrequencyX = profile.terrainFrequencyX();
        terrainFrequencyZ = profile.terrainFrequencyZ();
    }

    @Value
    private static class ChunkCoord {
        int x;
        int z;
    }

    @Override
    public ChunkData generateChunkData(World world, Random bukkitRandom, int chunkX, int chunkZ, BiomeGrid biomeGrid) {
        ChunkData chunk = createChunkData(world);

        Biome biome = profile.biome();
        // Internal resolution of biomes is 4
        for (int x = 0; x < 16; x += 4) {
            for (int z = 0; z < 16; z += 4) {
                for (int y = 0; y < 256; y += 4) {
                    biomeGrid.setBiome(x, y, z, biome);
                }
            }
        }

        ChunkCoord chunkCoord = new ChunkCoord(chunkX, chunkZ);

        if (BARRIER_COORDS.contains(chunkCoord)) {
            chunk.setRegion(0, 0, 0, 16, 256, 16, Material.BARRIER);
            return chunk;
        }

        if (!ARENA_COORDS.contains(chunkCoord)) {
            return chunk;
        }

        for (int subChunkX = 0; subChunkX < 16; subChunkX++) {
            for (int subChunkZ = 0; subChunkZ < 16; subChunkZ++) {
                int x = chunkX * 16 + subChunkX;

                int trueZ = chunkZ * 16 + subChunkZ;
                int z = trueZ;
                if (z < 0) {
                    z =  -z - 1;
                }

                int grassHeight = terrainHeight(x, z);
                int dirtHeight = grassHeight - 1;
                int stoneHeight = dirtHeight - 3;
                int voidHeight = stoneHeight - 10;

                Material grassMaterial = grassMaterial(x, trueZ);

                for (int y = voidHeight; y < stoneHeight; y++) {
                    Material material = stoneMaterial(x, y, z);
                    chunk.setBlock(subChunkX, y, subChunkZ, material);
                }

                for (int y = stoneHeight; y < grassHeight; y++) {
                    Material material;
                    if (grassMaterial == Material.GRASS_BLOCK || grassMaterial == Material.MYCELIUM) {
                        material = Material.DIRT;
                    } else {
                        material = profile.dirt();

                        if (material != Material.COARSE_DIRT) {
                            material = Material.DIRT;
                        }
                    }

                    chunk.setBlock(subChunkX, y, subChunkZ, material);
                }

                chunk.setRegion(subChunkX, dirtHeight, subChunkZ, subChunkX + 1, grassHeight, subChunkZ + 1, grassMaterial);
            }
        }

        return chunk;
    }

    private Material stoneMaterial(int x, int y, int z) {
        double scaledDistance = 0.4 + Math.pow(x * x + z * z, 0.25) / 16.0;
        double noise = Noise.gradientCoherentNoise3D(x / 3.0, y / 3.0, z / 3.0, seed + 1, NoiseQuality.BEST);

        if (noise > 1 - 0.85 * (1 - scaledDistance)) {
            return profile.blackstone();
        } else if (noise > scaledDistance) {
            return profile.basalt();
        } else {
            return profile.stone();
        }
    }

    private Material grassMaterial(int x, int z) {
        double noise = Noise.valueCoherentNoise3D(x / 5.0, 0, 0, seed + 1, NoiseQuality.BEST);
        int effectiveZ = round(z + (noise - 0.5) * 6);

        Random random = new Random(List.of(x, z, seed).hashCode());
        if (effectiveZ < 0) {
            double dirtChance = (-effectiveZ - 14) * profile.dirtSpreadFactor();
            if (Math.abs(random.nextGaussian()) > dirtChance) {
                return profile.dirt();
            }

            return Material.MYCELIUM;
        } else {
            double dirtChance = (effectiveZ - 13) / 2.0;
            if (Math.abs(random.nextGaussian()) > dirtChance) {
                return profile.dirt();
            }

            return Material.GRASS_BLOCK;
        }
    }

    private int terrainHeight(int x, int z) {
        double noise = Noise.gradientCoherentNoise3D(x * terrainFrequencyX, 0, z * terrainFrequencyZ, seed, NoiseQuality.BEST);
        return (int) (noise * terrainScale + HEIGHT_OFFSET);
    }

    public static int round(double value) {
        return (int) Math.round(value);
    }

    @Override
    public Location getFixedSpawnLocation(World world, Random random) {
        return world.getHighestBlockAt(0, 0).getLocation();
    }

    @Override
    public boolean canSpawn(World world, int x, int z) {
        return true;
    }

    @Override
    public boolean shouldGenerateCaves() {
        return false;
    }

    @Override
    public boolean shouldGenerateDecorations() {
        return false;
    }

    @Override
    public boolean shouldGenerateMobs() {
        return false;
    }

    @Override
    public boolean shouldGenerateStructures() {
        return false;
    }
}

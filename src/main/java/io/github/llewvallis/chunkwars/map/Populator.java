package io.github.llewvallis.chunkwars.map;

import io.github.llewvallis.chunkwars.team.GameTeam;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;

import java.util.*;

import static io.github.llewvallis.chunkwars.map.TerrainGenerator.round;

public abstract class Populator {

    protected final int seed;
    protected final TerrainProfile profile;

    protected World world;
    protected Chunk chunk;
    protected Random random;
    protected GameTeam team;

    protected Map<Location, BlockData> changes;

    protected boolean sideSwapProtection = true;

    public Populator(int seed) {
        this.seed = seed;
        profile = new TerrainProfile(seed);
    }

    public Runnable populate(World world, Chunk source) {
        this.world = world;
        this.chunk = source;
        team = source.getZ() < 0 ? GameTeam.DARK : GameTeam.LIGHT;
        random = new Random(seed + source.getX());
        changes = new HashMap<>();

        populate();

        Set<Map.Entry<Location, BlockData>> entries = new HashSet<>(changes.entrySet());
        return () -> entries.forEach(entry -> entry.getKey().getBlock().setBlockData(entry.getValue()));
    }

    protected abstract void populate();

    protected Material getBlock(double x, double y, double z) {
        int intX = round(x);
        int intZ = round(z);
        int intY = round(y);

        if (team == GameTeam.DARK) {
            intZ = 15 - intZ;
        }

        Location location = new Location(world, intX + chunk.getX() * 16, intY, intZ + chunk.getZ() * 16);
        return Optional.ofNullable(changes.get(location))
                .map(BlockData::getMaterial)
                .orElseGet(() -> location.getBlock().getType());
    }

    protected void setBlock(double x, double y, double z, Material material) {
        setBlock(x, y, z, material.createBlockData());
    }

    protected void setBlock(double x, double y, double z, BlockData blockData) {
        int intX = round(x);
        int intZ = round(z);
        int intY = round(y);

        if (team == GameTeam.DARK) {
            intZ = 15 - intZ;
        }

        if (sideSwapProtection && (intZ + chunk.getZ() * 16 < 0) != (team == GameTeam.DARK)) {
            return;
        }

        int worldX = intX + chunk.getX() * 16;
        int worldZ = intZ + chunk.getZ() * 16;

        changes.put(new Location(world, worldX, intY, worldZ), blockData);
    }

    protected int getTerrainHeight(double x, double z) {
        int intX = round(x);
        int intZ = round(z);

        if (team == GameTeam.DARK) {
            intZ = 15 - intZ;
        }

        return world.getHighestBlockYAt(intX + chunk.getX() * 16, intZ + chunk.getZ() * 16);
    }
}

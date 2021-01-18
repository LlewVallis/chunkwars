package io.github.llewvallis.chunkwars.map;

import org.bukkit.Material;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ScorchedIronPopulator extends Populator {

    private static final int SCORCHED_IRON_COUNT = 2;

    public ScorchedIronPopulator(int seed) {
        super(seed);
    }

    @Override
    protected void populate() {
        if ((chunk.getX() != 0 && chunk.getX() != -1) || (chunk.getZ() != -1 && chunk.getZ() != 0)) {
            return;
        }

        List<Vector> eligibleLocations = new ArrayList<>();

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int y = getTerrainHeight(x, z);
                if (getBlock(x, y, z) == Material.NETHERRACK) {
                    eligibleLocations.add(new Vector(x, y, z));
                }
            }
        }

        Collections.shuffle(eligibleLocations, random);

        for (int i = 0; i < SCORCHED_IRON_COUNT && i < eligibleLocations.size(); i++) {
            Vector location = eligibleLocations.get(i);
            setBlock(location.getX(), location.getY(), location.getZ(), Material.NETHER_QUARTZ_ORE);
        }
    }
}

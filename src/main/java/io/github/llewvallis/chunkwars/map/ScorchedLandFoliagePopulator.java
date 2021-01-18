package io.github.llewvallis.chunkwars.map;

import io.github.llewvallis.chunkwars.team.GameTeam;
import org.bukkit.Material;

import java.util.Random;

public class ScorchedLandFoliagePopulator extends Populator {

    public ScorchedLandFoliagePopulator(int seed) {
        super(seed);
    }

    @Override
    protected void populate() {
        random = new Random(seed + (team == GameTeam.DARK ? 1 : 0));

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z< 16; z++) {
                if (profile.shouldApplyScorchedFoliage()) {
                    int y = getTerrainHeight(x, z);

                    Material surfaceMaterial = getBlock(x, y, z);
                    if (surfaceMaterial != Material.NETHERRACK) {
                        continue;
                    }

                    setBlock(x, y, z, Material.CRIMSON_NYLIUM);
                    if (profile.shouldAddCrimsonRoots()) {
                        setBlock(x, y + 1, z, Material.CRIMSON_ROOTS);
                    }
                }
            }
        }
    }
}

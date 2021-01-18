package io.github.llewvallis.chunkwars.map;

import io.github.llewvallis.chunkwars.team.GameTeam;
import org.bukkit.Material;

import java.util.Random;

public class FoliagePopulator extends Populator {

    public FoliagePopulator(int seed) {
        super(seed);
    }

    @Override
    protected void populate() {
        random = new Random(seed + (team == GameTeam.DARK ? 1 : 0));

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z< 16; z++) {
                if (profile.shouldApplyFoliage()) {
                    int y = getTerrainHeight(x, z);

                    Material surfaceMaterial = getBlock(x, y, z);
                    if (surfaceMaterial == Material.MYCELIUM) {
                        setBlock(x, y + 1, z, profile.mushroom());
                    } else if (surfaceMaterial == Material.GRASS_BLOCK) {
                        setBlock(x, y + 1, z, profile.flower());
                    } else {
                        if (surfaceMaterial == Material.DIRT || surfaceMaterial == Material.COARSE_DIRT || surfaceMaterial == Material.PODZOL) {
                            setBlock(x, y + 1, z, Material.DEAD_BUSH);
                        }
                    }
                }
            }
        }
    }
}

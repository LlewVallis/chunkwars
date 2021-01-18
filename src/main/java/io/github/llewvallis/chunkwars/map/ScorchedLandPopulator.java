package io.github.llewvallis.chunkwars.map;

import org.bukkit.Material;

public class ScorchedLandPopulator extends Populator {

    private static final int RADIUS = 8;

    public ScorchedLandPopulator(int seed) {
        super(seed);
    }

    @Override
    protected void populate() {
        if ((chunk.getX() != 0 && chunk.getX() != -1) || (chunk.getZ() != -1 && chunk.getZ() != 0)) {
            return;
        }

        double baseX = random.nextDouble() / 2 - 0.25;
        double baseZ = random.nextDouble() / 2 - 0.25;

        if (chunk.getX() < 0) {
            baseX = 15 - baseX;
        }

        double baseY = getTerrainHeight(baseX, baseZ);

        for (double x = baseX - RADIUS; x < baseX + RADIUS; x++) {
            for (double z = baseZ - RADIUS; z < baseZ + RADIUS; z++) {
                for (double y = baseY - RADIUS; y < baseY + RADIUS && y <= getTerrainHeight(x, z); y++) {
                    double biasedDistance = Math.abs(random.nextGaussian()) * 3 +
                            Math.sqrt(Math.pow(baseX - x, 2) +
                            Math.pow(baseY - y, 2) +
                            Math.pow(baseZ - z, 2));

                    if (biasedDistance > RADIUS) {
                        continue;
                    }

                    setBlock(x, y, z, Material.NETHERRACK);
                }
            }
        }
    }
}

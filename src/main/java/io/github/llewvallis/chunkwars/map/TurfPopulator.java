package io.github.llewvallis.chunkwars.map;

import org.bukkit.Material;

public class TurfPopulator extends Populator {

    public TurfPopulator(int seed) {
        super(seed);
    }

    @Override
    protected void populate() {
        if (chunk.getX() != 0 || (chunk.getZ() != 0 && chunk.getZ() != -1)) {
            return;
        }

        double baseX = random.nextInt(4) + 8 + random.nextDouble() / 2 - 0.25;
        double baseZ = random.nextInt(3) + 7 + random.nextDouble() / 2 - 0.25;

        for (int i = 0; i < 25; i++) {
            double x = baseX + random.nextGaussian();
            double z = baseZ + random.nextGaussian();

            if (Math.sqrt(Math.pow(baseX - x, 2) + Math.pow(baseZ - z, 2)) > 5) {
                continue;
            }

            int y = getTerrainHeight(x, z);
            setBlock(x, y, z, Material.GRASS_PATH);
        }
    }
}

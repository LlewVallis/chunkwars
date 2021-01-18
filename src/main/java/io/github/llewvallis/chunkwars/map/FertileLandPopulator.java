package io.github.llewvallis.chunkwars.map;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.type.Farmland;

import java.util.ArrayList;
import java.util.List;

public class FertileLandPopulator extends Populator {

    public FertileLandPopulator(int seed) {
        super(seed);
    }

    @Override
    protected void populate() {
        if (chunk.getX() != -1 || (chunk.getZ() != 0 && chunk.getZ() != -1)) {
            return;
        }

        double baseX = random.nextInt(4) + 4 + random.nextDouble() / 2 - 0.25;
        double baseZ = random.nextInt(3) + 5.5 + random.nextDouble() / 2 - 0.25;

        List<Location> farmlandBlocks = new ArrayList<>();

        for (int i = 0; i < 20; i++) {
            double x = baseX + random.nextGaussian() * 1.5;
            double z = baseZ + random.nextGaussian() * 1.5;

            if (Math.sqrt(Math.pow(baseX - x, 2) + Math.pow(baseZ - z, 2)) > 5) {
                continue;
            }

            int y = getTerrainHeight(x, z);

            Farmland blockData = (Farmland) Material.FARMLAND.createBlockData();
            blockData.setMoisture(blockData.getMaximumMoisture());
            setBlock(x, y, z, blockData);

            farmlandBlocks.add(new Location(world, x, y, z));
        }

        int melonIndex = random.nextInt(farmlandBlocks.size());
        Location melonLocation = farmlandBlocks.get(melonIndex);

        Ageable melonData = (Ageable) Material.MELON_STEM.createBlockData();
        melonData.setAge(melonData.getMaximumAge());

        setBlock(melonLocation.getX(), melonLocation.getY() + 1, melonLocation.getZ(), melonData);
    }
}

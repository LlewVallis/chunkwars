package io.github.llewvallis.chunkwars.map;

import io.github.llewvallis.chunkwars.MathUtil;
import org.bukkit.Axis;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Orientable;
import org.bukkit.util.Vector;

import java.util.*;

import static io.github.llewvallis.chunkwars.map.TerrainGenerator.round;

public class CenterMinePopulator extends Populator {

    private static final int BLACKSTONE_COUNT = 4;
    private static final int NETHER_WOOD_COUNT = 5;

    private final Random detailRandom = new Random();

    public CenterMinePopulator(int seed) {
        super(seed);
    }

    @Override
    protected void populate() {
        if ((chunk.getX() != 0 && chunk.getX() != -1) || (chunk.getZ() != -1 && chunk.getZ() != 0)) {
            return;
        }

        double x = random.nextInt(2) + random.nextDouble() / 2 - 0.25;
        double z = random.nextInt(2) + random.nextDouble() / 2 - 0.25;

        if (chunk.getX() < 0) {
            x = 15 - x;
        }

        int y = getTerrainHeight(x, z);
        generateMine(x, y - 2, z);
    }

    private void generateMine(double baseX, int baseY, double baseZ) {
        double slantX = random.nextDouble() * 2 - 1;
        double slantZ = random.nextDouble() * 2 - 1;

        Set<Vector> basaltLocations = new HashSet<>();

        for (double x = baseX - 4; x <= baseX + 4; x++) {
            if (round(x) > 15 || round(x) < 0) {
                continue;
            }

            for (double z = baseZ - 4; z <= baseZ + 4; z++) {

                double deltaX = x - baseX;
                double deltaZ = z - baseZ;

                double height = 8 - 3 * Math.hypot(deltaX, deltaZ);
                height += slantX * deltaX;
                height += slantZ * deltaZ;

                int terrainLevel = getTerrainHeight(x, z);

                for (int y = baseY; y <= baseY + height; y++) {
                    if (y >= terrainLevel || random.nextInt(3) == 0) {
                        BlockData block = basaltBlockData();
                        setBlock(x, y, z, block);

                        basaltLocations.add(MathUtil.roundVector(new Vector(x, y, z)));
                    }
                }
            }
        }

        modifyBasalt(baseX, baseY, baseZ, basaltLocations);
    }

    private void modifyBasalt(double baseX, double baseY, double baseZ, Set<Vector> basaltLocations) {
        Vector baseLocation = new Vector(baseX, baseY, baseZ);

        for (int i = 0; i < BLACKSTONE_COUNT; i++) {
            Vector blackstoneLocation = basaltLocations.stream()
                    .min(Comparator.comparingDouble(location -> location.distanceSquared(baseLocation)))
                    .orElse(null);

            if (blackstoneLocation == null) {
                break;
            }

            setBlock(blackstoneLocation.getX(), blackstoneLocation.getY(), blackstoneLocation.getZ(), profile.blackstone());
            basaltLocations.remove(blackstoneLocation);
        }

        List<Vector> basaltLocationsList = new ArrayList<>(basaltLocations);
        basaltLocationsList.removeIf(location -> location.distanceSquared(baseLocation) < 2);

        Collections.shuffle(basaltLocationsList, random);

        for (int i = 0; i < NETHER_WOOD_COUNT && i < basaltLocationsList.size(); i ++) {
            Vector netherWoodLocation = basaltLocationsList.get(i);
            setBlock(netherWoodLocation.getX(), netherWoodLocation.getY(), netherWoodLocation.getZ(), profile.netherWood());
        }
    }

    private BlockData basaltBlockData() {
        Material basalt = profile.basalt();
        Axis axis = detailRandom.nextBoolean() ? Axis.Y : (detailRandom.nextBoolean() ? Axis.X : Axis.Z);
        Orientable basaltBlockData = (Orientable) basalt.createBlockData();
        basaltBlockData.setAxis(axis);

        return basaltBlockData;
    }
}

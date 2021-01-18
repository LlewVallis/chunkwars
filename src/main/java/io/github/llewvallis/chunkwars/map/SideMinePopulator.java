package io.github.llewvallis.chunkwars.map;

import io.github.llewvallis.chunkwars.MathUtil;
import org.bukkit.Material;
import org.bukkit.util.Vector;

import java.util.*;

public class SideMinePopulator extends Populator {

    private static final int IRON_ORE_COUNT = 5;

    public SideMinePopulator(int seed) {
        super(seed);
    }

    @Override
    protected void populate() {
        if (chunk.getX() != -1 || (chunk.getZ() != -2 && chunk.getZ() != 1)) {
            return;
        }

        double x = random.nextInt(8) + 4 + random.nextDouble() / 2 - 0.25;
        double z = random.nextInt(8) + 4 + random.nextDouble() / 2 - 0.25;
        int y = getTerrainHeight(x, z);

        Set<Vector> potentialOreLocations = new HashSet<>();

        generateMine(x, y - 1, z, potentialOreLocations);

        double secondaryDistance = 2 + random.nextDouble() * 2;
        double secondaryDirection = Math.PI * 2 * random.nextDouble();

        double secondaryX = x + Math.cos(secondaryDirection) * secondaryDistance;
        double secondaryZ = z + Math.sin(secondaryDirection) * secondaryDistance;

        if (secondaryX > 12.25) secondaryX -= secondaryDistance;
        if (secondaryX < 2.75) secondaryX += secondaryDistance;
        if (secondaryZ > 12.25) secondaryZ -= secondaryDistance;
        if (secondaryZ < 2.75) secondaryZ += secondaryDistance;

        int secondaryY = getTerrainHeight(secondaryX, secondaryZ);

        generateMine(secondaryX, secondaryY - 3, secondaryZ, potentialOreLocations);

        List<Vector> potentialOreLocationsList = new ArrayList<>(potentialOreLocations);
        Collections.shuffle(potentialOreLocationsList, random);

        for (int i = 0; i < IRON_ORE_COUNT && i < potentialOreLocationsList.size(); i++) {
            Vector location = potentialOreLocationsList.get(i);
            setBlock(location.getX(), location.getY(), location.getZ(), Material.IRON_ORE);
        }
    }

    private void generateMine(double baseX, int baseY, double baseZ, Set<Vector> potentialOreLocations) {
        double slantX = random.nextDouble() * 2 - 1;
        double slantZ = random.nextDouble() * 2 - 1;

        for (double x = baseX - 4; x <= baseX + 4; x++) {
            for (double z = baseZ - 4; z <= baseZ + 4; z++) {
                double deltaX = x - baseX;
                double deltaZ = z - baseZ;

                double height = 8 - 3 * Math.hypot(deltaX, deltaZ);
                height += slantX * deltaX;
                height += slantZ * deltaZ;

                int terrainLevel = getTerrainHeight(x, z);

                for (int y = baseY; y <= baseY + height; y++) {
                    if (y >= terrainLevel || random.nextInt(3) == 0) {
                        setBlock(x, y, z, profile.stone());
                        potentialOreLocations.add(MathUtil.roundVector(new Vector(x, y, z)));
                    }
                }
            }
        }
    }
}

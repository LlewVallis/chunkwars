package io.github.llewvallis.chunkwars.map;

import lombok.experimental.UtilityClass;
import lombok.extern.java.Log;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.WorldCreator;

import java.util.*;

@Log
@UtilityClass
public class MapGenerator {

    public World generate() {
        int seed = new Random().nextInt();

        String worldName = "arena-" + UUID.randomUUID();
        log.info("Generating " + worldName);

        World world = new WorldCreator(worldName)
                .generator(new TerrainGenerator(seed))
                .createWorld();

        world.setAutoSave(false);

        Set<Chunk> chunks = new HashSet<>();

        for (int x = -1; x <= 0 ; x++) {
            for (int z = -2; z <= 1; z++) {
                chunks.add(world.getChunkAt(x, z));
            }
        }

        log.info("Populating chunks");
        List<Populator> populators = List.of(
                new ScorchedLandPopulator(seed),
                new FertileLandPopulator(seed + 1),
                new TurfPopulator(seed + 2),
                new SideMinePopulator(seed + 3),
                new TreePopulator(seed + 4),
                new CenterMinePopulator(seed + 5),
                new ScorchedIronPopulator(seed + 6),
                new ScorchedLandFoliagePopulator(seed + 7),
                new FoliagePopulator(seed + 8)
        );

        for (Populator populator : populators) {
            Set<Runnable> changes = new HashSet<>();

            for (Chunk chunk : chunks) {
                changes.add(populator.populate(world, chunk));
            }

            changes.forEach(Runnable::run);
        }

        ArtifactGenerator.generate(world);
        return world;
    }
}

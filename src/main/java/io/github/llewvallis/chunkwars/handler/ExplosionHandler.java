package io.github.llewvallis.chunkwars.handler;

import io.github.llewvallis.chunkwars.ChunkWarsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class ExplosionHandler implements Listener {

    private static final Random random = new Random();

    private static final Set<Material> IMMUNE_BLOCKS = Set.of(
            Material.BLACKSTONE,
            Material.POLISHED_BLACKSTONE,
            Material.POLISHED_BLACKSTONE_BRICKS,
            Material.CRACKED_POLISHED_BLACKSTONE_BRICKS
    );

    private static final List<Material> STONE_RUBBLE_BLOCKS = List.of(
            Material.DEAD_BUBBLE_CORAL_BLOCK,
            Material.DEAD_FIRE_CORAL_BLOCK,
            Material.DEAD_HORN_CORAL_BLOCK
    );

    private static final Set<Material> RESISTANT_STONE_BLOCKS = Set.of(
            Material.COBBLESTONE,
            Material.STONE,
            Material.ANDESITE,
            Material.IRON_ORE,
            Material.COAL_ORE,
            Material.BASALT,
            Material.POLISHED_BASALT
    );

    private static final List<Material> DIRT_RUBBLE_BLOCKS = List.of(
            Material.LIGHT_GRAY_CONCRETE_POWDER,
            Material.GRAVEL
    );

    private static final Set<Material> RESISTANT_DIRT_BLOCKS = Set.of(
            Material.DIRT,
            Material.COARSE_DIRT,
            Material.GRASS_BLOCK,
            Material.MYCELIUM,
            Material.PODZOL,
            Material.GRASS_PATH,
            Material.FARMLAND,
            Material.SOUL_SAND,
            Material.SOUL_SOIL,
            Material.NETHERRACK,
            Material.CRIMSON_NYLIUM,
            Material.WARPED_NYLIUM,
            Material.NETHER_QUARTZ_ORE,
            Material.NETHER_GOLD_ORE
    );

    @EventHandler
    private void onExplosion(EntityExplodeEvent e) {
        Iterator<Block> iterator = e.blockList().iterator();

        while (iterator.hasNext()) {
            Block block = iterator.next();
            Material material = block.getType();

            if (IMMUNE_BLOCKS.contains(material) ||
                    STONE_RUBBLE_BLOCKS.contains(material) ||
                    DIRT_RUBBLE_BLOCKS.contains(material)) {
                iterator.remove();
                continue;
            }

            List<Material> replacementCandidates;

            if (RESISTANT_STONE_BLOCKS.contains(material)) {
                replacementCandidates = STONE_RUBBLE_BLOCKS;
                iterator.remove();
            } else if (RESISTANT_DIRT_BLOCKS.contains(material)) {
                replacementCandidates = DIRT_RUBBLE_BLOCKS;
                iterator.remove();
            } else {
                continue;
            }

            String originalBlockNbt = block.getBlockData().getAsString();
            Material replacement = replacementCandidates.get(random.nextInt(replacementCandidates.size()));

            Bukkit.getScheduler().runTaskLater(ChunkWarsPlugin.instance, () -> {
                block.setType(replacement);
                block.setMetadata(Restoration.RESTORATION_META_KEY, new FixedMetadataValue(ChunkWarsPlugin.instance, originalBlockNbt));
            }, 0);
        }
    }
}

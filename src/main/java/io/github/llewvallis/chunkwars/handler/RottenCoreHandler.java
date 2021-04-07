package io.github.llewvallis.chunkwars.handler;

import io.github.llewvallis.chunkwars.InventoryUtil;
import io.github.llewvallis.chunkwars.world.ArenaPool;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Map;
import java.util.Set;

public class RottenCoreHandler implements Listener {

    private static final Map<Material, Material> MAPPINGS = Map.ofEntries(
            Map.entry(Material.ACACIA_WOOD, Material.CRIMSON_HYPHAE),
            Map.entry(Material.BIRCH_WOOD, Material.CRIMSON_HYPHAE),
            Map.entry(Material.DARK_OAK_WOOD, Material.CRIMSON_HYPHAE),
            Map.entry(Material.JUNGLE_WOOD, Material.CRIMSON_HYPHAE),
            Map.entry(Material.OAK_WOOD, Material.CRIMSON_HYPHAE),
            Map.entry(Material.SPRUCE_WOOD, Material.CRIMSON_HYPHAE),
            Map.entry(Material.STRIPPED_ACACIA_WOOD, Material.CRIMSON_HYPHAE),
            Map.entry(Material.STRIPPED_BIRCH_WOOD, Material.CRIMSON_HYPHAE),
            Map.entry(Material.STRIPPED_DARK_OAK_WOOD, Material.CRIMSON_HYPHAE),
            Map.entry(Material.STRIPPED_JUNGLE_WOOD, Material.CRIMSON_HYPHAE),
            Map.entry(Material.STRIPPED_OAK_WOOD, Material.CRIMSON_HYPHAE),
            Map.entry(Material.STRIPPED_SPRUCE_WOOD, Material.CRIMSON_HYPHAE),
            Map.entry(Material.ACACIA_LEAVES, Material.COBWEB),
            Map.entry(Material.BIRCH_LEAVES, Material.COBWEB),
            Map.entry(Material.DARK_OAK_LEAVES, Material.COBWEB),
            Map.entry(Material.JUNGLE_LEAVES, Material.COBWEB),
            Map.entry(Material.OAK_LEAVES, Material.COBWEB),
            Map.entry(Material.SPRUCE_LEAVES, Material.COBWEB),
            Map.entry(Material.DIRT, Material.NETHERRACK),
            Map.entry(Material.COARSE_DIRT, Material.NETHERRACK),
            Map.entry(Material.PODZOL, Material.CRIMSON_NYLIUM),
            Map.entry(Material.GRASS_BLOCK, Material.CRIMSON_NYLIUM),
            Map.entry(Material.MYCELIUM, Material.WARPED_NYLIUM),
            Map.entry(Material.FARMLAND, Material.SOUL_SAND),
            Map.entry(Material.GRASS_PATH, Material.SOUL_SOIL)
    );

    private static final Set<Material> FOLIAGE_CARRIERS = Set.of(
            Material.PODZOL,
            Material.GRASS_BLOCK,
            Material.MYCELIUM,
            Material.FARMLAND
    );

    private static final Map<Material, Material> FOLIAGE_MAPPINGS = Map.of(
            Material.MELON_STEM, Material.NETHER_WART,
            Material.RED_MUSHROOM, Material.WARPED_ROOTS,
            Material.BROWN_MUSHROOM, Material.WARPED_ROOTS,
            Material.DEAD_BUSH, Material.CRIMSON_ROOTS,
            Material.CORNFLOWER, Material.CRIMSON_ROOTS,
            Material.BLUE_ORCHID, Material.CRIMSON_ROOTS,
            Material.ALLIUM, Material.CRIMSON_ROOTS
    );

    @EventHandler
    private void onUseRottenCore(PlayerInteractEvent e) {
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        if (e.getItem() == null || e.getItem().getType() != Material.MAGMA_CREAM) {
            return;
        }

        boolean replacedBlock = false;

        for (int deltaX = -1; deltaX <= 1; deltaX++) {
            for (int deltaY = -1; deltaY <= 1; deltaY++) {
                for (int deltaZ = -1; deltaZ <= 1; deltaZ++) {
                    if (deltaX != 0 && deltaY != 0 && deltaZ != 0) {
                        continue;
                    }

                    Block block = e.getClickedBlock().getRelative(deltaX, deltaY, deltaZ);

                    Material newType = MAPPINGS.get(block.getType());
                    if (newType == null) {
                        continue;
                    }

                    Block blockAbove = block.getRelative(BlockFace.UP);
                    if (FOLIAGE_MAPPINGS.containsKey(blockAbove.getType())) {
                        if (FOLIAGE_CARRIERS.contains(block.getType())) {
                            Material newMaterial = FOLIAGE_MAPPINGS.get(blockAbove.getType());
                            blockAbove.setType(newMaterial);

                            if (newMaterial == Material.NETHER_WART) {
                                SporeHandler.growSpore(blockAbove.getLocation());
                            }
                        } else {
                            blockAbove.setType(Material.AIR);
                        }
                    }

                    block.setType(newType);
                    replacedBlock = true;
                }
            }
        }

        if (replacedBlock) {
            Location location = e.getClickedBlock().getLocation();
            location.getWorld().playSound(location, Sound.ENTITY_BEE_STING, SoundCategory.BLOCKS, 2, 1.66f);

            if (e.getPlayer().getGameMode() == GameMode.SURVIVAL && ArenaPool.instance.inArena(e.getPlayer())) {
                InventoryUtil.removeHeldItem(e.getPlayer());
            }
        }
    }
}

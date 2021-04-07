package io.github.llewvallis.chunkwars.handler;

import io.github.llewvallis.chunkwars.ChunkWarsPlugin;
import io.github.llewvallis.chunkwars.ItemBuilder;
import io.github.llewvallis.chunkwars.world.ArenaPool;
import lombok.RequiredArgsConstructor;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ResourceHandler implements Listener {

    private final Set<Resource> resources = Set.of(
            new Resource(Material.DARK_OAK_WOOD, Material.STRIPPED_DARK_OAK_WOOD, 600,
                    ItemBuilder.wood(), ChatColor.DARK_GREEN, "Wood"),
            new Resource(Material.SPRUCE_WOOD, Material.STRIPPED_SPRUCE_WOOD, 600,
                    ItemBuilder.wood(), ChatColor.DARK_GREEN, "Wood"),
            new Resource(Material.JUNGLE_WOOD, Material.STRIPPED_JUNGLE_WOOD, 600,
                    ItemBuilder.wood(), ChatColor.DARK_GREEN, "Wood"),
            new Resource(Material.BIRCH_WOOD, Material.STRIPPED_BIRCH_WOOD, 600,
                    ItemBuilder.wood(), ChatColor.DARK_GREEN, "Wood"),
            new Resource(Material.ACACIA_WOOD, Material.STRIPPED_ACACIA_WOOD, 600,
                    ItemBuilder.wood(), ChatColor.DARK_GREEN, "Wood"),
            new Resource(Material.OAK_WOOD, Material.STRIPPED_OAK_WOOD, 600,
                    ItemBuilder.wood(), ChatColor.DARK_GREEN, "Wood"),

            new Resource(Material.CRIMSON_HYPHAE, Material.STRIPPED_CRIMSON_HYPHAE, 600,
                    ItemBuilder.wood().amount(3), ChatColor.DARK_GREEN, "Wood"),
            new Resource(Material.WARPED_HYPHAE, Material.STRIPPED_WARPED_HYPHAE, 600,
                    ItemBuilder.wood().amount(3), ChatColor.DARK_GREEN, "Wood"),

            new Resource(Material.DIRT, null, 0, ItemBuilder.dirt(), ChatColor.GOLD, "Dirt"),
            new Resource(Material.GRASS_BLOCK, null, 0, ItemBuilder.dirt(), ChatColor.GOLD, "Dirt"),
            new Resource(Material.MYCELIUM, null, 0, ItemBuilder.dirt(), ChatColor.GOLD, "Dirt"),
            new Resource(Material.PODZOL, null, 0, ItemBuilder.dirt(), ChatColor.GOLD, "Dirt"),
            new Resource(Material.COARSE_DIRT, null, 0, ItemBuilder.dirt(), ChatColor.GOLD, "Dirt"),
            new Resource(Material.NETHERRACK, null, 0, ItemBuilder.dirt().amount(3), ChatColor.GOLD, "Dirt"),
            new Resource(Material.CRIMSON_NYLIUM, null, 0, ItemBuilder.dirt().amount(3), ChatColor.GOLD, "Dirt"),
            new Resource(Material.WARPED_NYLIUM, null, 0, ItemBuilder.dirt().amount(3), ChatColor.GOLD, "Dirt"),

            new Resource(Material.FARMLAND, null, 0, ItemBuilder.dirt().amount(2), ChatColor.GOLD, "Dirt"),
            new Resource(Material.GRASS_PATH, null, 0, ItemBuilder.dirt().amount(2), ChatColor.GOLD, "Dirt"),

            new Resource(Material.STONE, null, 0, ItemBuilder.stone(), ChatColor.GRAY, "Stone"),
            new Resource(Material.COBBLESTONE, null, 0, ItemBuilder.stone(), ChatColor.GRAY, "Stone"),
            new Resource(Material.ANDESITE, null, 0, ItemBuilder.stone(), ChatColor.GRAY, "Stone"),

            new Resource(Material.BASALT, null, 0, ItemBuilder.stone().amount(2), ChatColor.GRAY, "Stone"),
            new Resource(Material.POLISHED_BASALT, null, 0, ItemBuilder.stone().amount(2), ChatColor.GRAY, "Stone"),

            new Resource(Material.POLISHED_BLACKSTONE_BRICKS, Material.CRACKED_POLISHED_BLACKSTONE_BRICKS, 1200,
                    ItemBuilder.stone().amount(5), ChatColor.GRAY, "Stone"),
            new Resource(Material.POLISHED_BLACKSTONE, Material.BLACKSTONE, 1200,
                    ItemBuilder.stone().amount(5), ChatColor.GRAY, "Stone"),

            new Resource(Material.IRON_ORE, Material.COAL_ORE, 1200,
                         ItemBuilder.iron(), ChatColor.GRAY, "Iron"),
            new Resource(Material.NETHER_QUARTZ_ORE, Material.NETHER_GOLD_ORE, 1200,
                    ItemBuilder.iron().amount(2), ChatColor.GRAY, "Iron")
    );

    private final Map<Material, Resource> resourceMappings = resources.stream()
            .collect(Collectors.toMap(
                    resource -> resource.source,
                    Function.identity()
            ));

    private final Map<Material, Resource> resourceMappingsInverse = resources.stream()
            .filter(resource -> resource.broken != null)
            .collect(Collectors.toMap(
                    resource -> resource.broken,
                    Function.identity()
            ));

    @RequiredArgsConstructor
    private static class Resource {
        private final Material source;
        private final Material broken;
        private final int regenTime;
        private final ItemBuilder item;
        private final ChatColor messageColor;
        private final String name;
    }

    @EventHandler
    private void onPistonPush(BlockPistonExtendEvent e) {
        onPistonMove(e.getBlocks(), e.getDirection());
    }

    @EventHandler
    private void onPistonPull(BlockPistonRetractEvent e) {
        onPistonMove(e.getBlocks(), e.getDirection());
    }

    private void onPistonMove(List<Block> blocks, BlockFace direction) {
        for (Block block : blocks) {
            Location destination = block.getLocation().add(direction.getDirection());
            Resource resource = resourceMappingsInverse.get(block.getType());

            if (resource != null) {
                Bukkit.getScheduler().scheduleSyncDelayedTask(ChunkWarsPlugin.instance, () -> {
                    if (destination.getBlock().getType() == resource.broken) {
                        regenerate(destination, resource.source, resource.regenTime / 2);
                    }
                }, 3);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    private void onResourceBreak(BlockBreakEvent e) {
        Block block = e.getBlock();
        Material material = block.getType();
        Player player = e.getPlayer();

        if (player.getGameMode() != GameMode.CREATIVE && ArenaPool.instance.inArena(block)) {
            if (resourceMappings.containsKey(material)) {
                Resource resource = resourceMappings.get(material);

                if (resource.broken != null) {
                    block.setType(resource.broken);
                    regenerate(block.getLocation(), material, resource.regenTime);
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_HAT, 1, 1);
                    e.setCancelled(true);
                }

                int multiplier = determineDropMultiplier(player.getInventory().getItemInMainHand().getType(), resource);
                ItemStack drop = resource.item.build();
                drop.setAmount(multiplier * drop.getAmount());

                player.getInventory().addItem(drop);

                String message = resource.messageColor + "+" + drop.getAmount() + " " + resource.name;
                player.sendActionBar(message);
            } else if (resourceMappingsInverse.containsKey(material)) {
                block.getWorld().playSound(block.getLocation(), Sound.ENTITY_ARMOR_STAND_BREAK, 1, 1);
            }
        }
    }

    private int determineDropMultiplier(Material tool, Resource resource) {
        if (tool == Material.IRON_SHOVEL && ToolHandler.DIRTS.contains(resource.source)) {
            return 16;
        }

        return 1;
    }

    private void regenerate(Location location, Material newBlock, int ticks) {
        new BlockRegenerator(location, newBlock, ticks).register();
    }
}

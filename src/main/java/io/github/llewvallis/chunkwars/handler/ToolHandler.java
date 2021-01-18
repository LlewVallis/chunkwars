package io.github.llewvallis.chunkwars.handler;

import io.github.llewvallis.chunkwars.ItemBuilder;
import io.github.llewvallis.chunkwars.world.ArenaPool;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ToolHandler implements Listener {

    public static final Set<Material> REFINED_LOGS = Set.of(
            Material.STRIPPED_CRIMSON_HYPHAE,
            Material.STRIPPED_WARPED_HYPHAE
    );

    public static final Set<Material> REFINED_LOG_TOOLS = Set.of(
            Material.IRON_AXE,
            Material.GOLDEN_AXE,
            Material.DIAMOND_AXE,
            Material.NETHERITE_AXE
    );

    public static final Set<Material> HARDENED_LOGS = Set.of(
            Material.STRIPPED_OAK_WOOD,
            Material.STRIPPED_SPRUCE_WOOD,
            Material.STRIPPED_BIRCH_WOOD,
            Material.STRIPPED_JUNGLE_WOOD,
            Material.STRIPPED_ACACIA_WOOD,
            Material.STRIPPED_DARK_OAK_WOOD
    );

    public static final Set<Material> HARDENED_LOG_TOOLS = new HashSet<>(Set.of(
            Material.STONE_AXE
    ));
    static { HARDENED_LOG_TOOLS.addAll(REFINED_LOG_TOOLS); }

    public static final Set<Material> LOGS = Set.of(
            Material.OAK_WOOD,
            Material.SPRUCE_WOOD,
            Material.BIRCH_WOOD,
            Material.JUNGLE_WOOD,
            Material.ACACIA_WOOD,
            Material.DARK_OAK_WOOD,
            Material.CRIMSON_HYPHAE,
            Material.WARPED_HYPHAE
    );

    public static final Set<Material> LOG_TOOLS = new HashSet<>(Set.of(
            Material.WOODEN_AXE
    ));
    static { LOG_TOOLS.addAll(HARDENED_LOG_TOOLS); }

    public static final Set<Material> REFINED_STONES = Set.of(
            Material.CRACKED_POLISHED_BLACKSTONE_BRICKS,
            Material.BLACKSTONE,
            Material.COAL_ORE,
            Material.NETHER_GOLD_ORE
    );

    public static final Set<Material> REFINED_STONE_TOOLS = Set.of(
            Material.IRON_PICKAXE,
            Material.GOLDEN_PICKAXE,
            Material.DIAMOND_PICKAXE,
            Material.NETHERITE_PICKAXE
    );

    public static final Set<Material> HARDENED_STONES = Set.of(
            Material.POLISHED_BLACKSTONE_BRICKS,
            Material.POLISHED_BLACKSTONE,
            Material.IRON_ORE,
            Material.NETHER_QUARTZ_ORE
    );

    public static final Set<Material> HARDENED_STONE_TOOLS = new HashSet<>(Set.of(
            Material.STONE_PICKAXE
    ));
    static { HARDENED_STONE_TOOLS.addAll(REFINED_STONE_TOOLS); }

    public static final Set<Material> STONES = Set.of(
            Material.STONE,
            Material.ANDESITE,
            Material.COBBLESTONE,
            Material.BASALT,
            Material.POLISHED_BASALT
    );

    public static final Set<Material> STONE_TOOLS = new HashSet<>(Set.of(
            Material.WOODEN_PICKAXE
    ));
    static { STONE_TOOLS.addAll(HARDENED_STONE_TOOLS); }

    public static final Set<Material> HARDENED_DIRTS = Set.of(
            Material.FARMLAND,
            Material.GRASS_PATH
    );

    public static final Set<Material> HARDENED_DIRT_TOOLS = Set.of(
            Material.STONE_SHOVEL,
            Material.IRON_SHOVEL,
            Material.GOLDEN_SHOVEL,
            Material.DIAMOND_SHOVEL,
            Material.NETHERITE_SHOVEL
    );

    public static final Set<Material> DIRTS = Set.of(
            Material.GRASS_BLOCK,
            Material.MYCELIUM,
            Material.PODZOL,
            Material.DIRT,
            Material.COARSE_DIRT
    );

   public static final Set<Material> DIRT_TOOLS = new HashSet<>(Set.of(
            Material.WOODEN_SHOVEL
    ));
   static { DIRT_TOOLS.addAll(HARDENED_DIRT_TOOLS); }

    private final Map<Material, Integer> toolUses = new HashMap<>();
    {
        toolUses.put(Material.WOODEN_SWORD, 5);
        toolUses.put(Material.WOODEN_PICKAXE, 10);
        toolUses.put(Material.WOODEN_AXE, 10);
        toolUses.put(Material.WOODEN_SHOVEL, 20);
        toolUses.put(Material.STONE_SWORD, 5);
        toolUses.put(Material.STONE_PICKAXE, 20);
        toolUses.put(Material.STONE_AXE, 20);
        toolUses.put(Material.STONE_SHOVEL, 40);
        toolUses.put(Material.IRON_SWORD, 5);
        toolUses.put(Material.IRON_PICKAXE, 40);
        toolUses.put(Material.IRON_AXE, 40);
        toolUses.put(Material.IRON_SHOVEL, 8);
    }

    @EventHandler(priority = EventPriority.LOW)
    private void onBlockBreak(BlockBreakEvent e) {
        Player player = e.getPlayer();

        if (player.getGameMode() == GameMode.SURVIVAL && ArenaPool.instance.inArena(player)) {
            PlayerInventory inventory = player.getInventory();
            ItemStack item = inventory.getItemInMainHand();

            Material usedMaterial = item.getType();
            Material brokenMaterial = e.getBlock().getType();

            boolean usedTool = false;

            if (REFINED_LOGS.contains(brokenMaterial)) {
                if (REFINED_LOG_TOOLS.contains(usedMaterial)) {
                    usedTool = true;
                } else {
                    e.setCancelled(true);
                    sendError(player, ItemBuilder.refinedAxe());
                }
            } else if (HARDENED_LOGS.contains(brokenMaterial)) {
                if (HARDENED_LOG_TOOLS.contains(usedMaterial)) {
                    usedTool = true;
                } else {
                    e.setCancelled(true);
                    sendError(player, ItemBuilder.hardenedAxe());
                }
            } else if (LOGS.contains(brokenMaterial) && LOG_TOOLS.contains(usedMaterial)) {
                usedTool = true;
            }

            if (REFINED_STONES.contains(brokenMaterial)) {
                if (REFINED_STONE_TOOLS.contains(usedMaterial)) {
                    usedTool = true;
                } else {
                    e.setCancelled(true);
                    sendError(player, ItemBuilder.refinedPickaxe());
                }
            } else if (HARDENED_STONES.contains(brokenMaterial)) {
                if (HARDENED_STONE_TOOLS.contains(usedMaterial)) {
                    usedTool = true;
                } else {
                    e.setCancelled(true);
                    sendError(player, ItemBuilder.hardenedPickaxe());
                }
            } else if (STONES.contains(brokenMaterial)) {
                if (STONE_TOOLS.contains(usedMaterial)) {
                    usedTool = true;
                } else {
                    e.setCancelled(true);
                    sendError(player, ItemBuilder.basicPickaxe());
                }
            }

            if (HARDENED_DIRTS.contains(brokenMaterial)) {
                if (HARDENED_DIRT_TOOLS.contains(usedMaterial)) {
                    usedTool = true;
                } else {
                    e.setCancelled(true);
                    sendError(player, ItemBuilder.hardenedShovel());
                }
            } else if (DIRTS.contains(brokenMaterial) && DIRT_TOOLS.contains(usedMaterial)) {
                usedTool = true;
            }

            if (usedTool) {
                ItemMeta meta = item.getItemMeta();
                Damageable damageable = (Damageable) meta;

                float uses = toolUses.getOrDefault(usedMaterial, 1);
                int maxDamage = usedMaterial.getMaxDurability();
                int newDamage = damageable.getDamage() + (int) Math.ceil(maxDamage / uses);

                damageable.setDamage(newDamage);
                item.setItemMeta(meta);

                if (newDamage < maxDamage) {
                    inventory.setItemInMainHand(item);
                } else {
                    inventory.setItemInMainHand(null);

                    Location soundLocation = player.getLocation();
                    soundLocation.add(0, Float.MAX_VALUE / 256, 0);
                    player.playSound(soundLocation, Sound.ENTITY_ITEM_BREAK, Float.MAX_VALUE, 1);
                }
            }
        }
    }

    private void sendError(Player player, ItemBuilder builder) {
        ItemStack stack = builder.build();
        player.sendMessage(ChatColor.RED + "A " + stack.getItemMeta().getDisplayName() + ChatColor.RED + " is required");

        Location soundLocation = player.getLocation();
        soundLocation.add(0, Float.MAX_VALUE / 256, 0);
        player.playSound(soundLocation, Sound.ENTITY_VILLAGER_NO, SoundCategory.MASTER, Float.MAX_VALUE, 1);
    }

    @EventHandler
    private void onToolDamage(PlayerItemDamageEvent e) {
        e.setCancelled(true);
    }
}

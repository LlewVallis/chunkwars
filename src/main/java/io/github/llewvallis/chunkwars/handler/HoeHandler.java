package io.github.llewvallis.chunkwars.handler;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Farmland;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Set;

public class HoeHandler implements Listener {

    private static final Set<Material> HOES = Set.of(
            Material.WOODEN_HOE,
            Material.STONE_HOE,
            Material.IRON_HOE,
            Material.GOLDEN_HOE,
            Material.DIAMOND_HOE,
            Material.NETHERITE_HOE
    );

    private static final Set<Material> TILLABLE_BLOCKS = Set.of(
            Material.GRASS_BLOCK,
            Material.MYCELIUM,
            Material.DIRT,
            Material.COARSE_DIRT,
            Material.PODZOL
    );

    private static final Set<Material> VANILLA_TILLABLE_BLOCKS = Set.of(
            Material.MYCELIUM,
            Material.PODZOL
    );

    private static final Set<Material> BREAKABLE_BLOCKS = Set.of(
            Material.CORNFLOWER,
            Material.BLUE_ORCHID,
            Material.ALLIUM,
            Material.RED_MUSHROOM,
            Material.BROWN_MUSHROOM,
            Material.DEAD_BUSH,
            Material.AIR
    );

    @EventHandler
    private void onHoe(PlayerInteractEvent e) {
        ItemStack stack = e.getItem();

        if (
                e.getAction() != Action.RIGHT_CLICK_BLOCK
                        || stack == null
                        || !HOES.contains(stack.getType())
        ) {
            return;
        }

        e.setCancelled(true);

        Block block = e.getClickedBlock();

        if (!TILLABLE_BLOCKS.contains(block.getType())) {
            return;
        }

        Block blockAbove = block.getRelative(BlockFace.UP);

        if (!BREAKABLE_BLOCKS.contains(blockAbove.getType())) {
            return;
        }

        blockAbove.setType(Material.AIR);

        Farmland blockData = (Farmland) Material.FARMLAND.createBlockData();
        blockData.setMoisture(blockData.getMaximumMoisture());
        block.setBlockData(blockData);

        if (!VANILLA_TILLABLE_BLOCKS.contains(block.getType())) {
            Location soundLocation = new Location(block.getWorld(), 0, Float.MAX_VALUE / 256, 0);
            e.getPlayer().playSound(soundLocation, Sound.ITEM_HOE_TILL, Float.MAX_VALUE, 1);
        }

        for (Player player : block.getWorld().getPlayers()) {
            if (player != e.getPlayer()) {
                player.playSound(block.getLocation(), Sound.ITEM_HOE_TILL, 1, 1);
            }
        }

        PlayerInventory inventory = e.getPlayer().getInventory();
        if (HOES.contains(inventory.getItemInMainHand().getType())) {
            inventory.setItemInMainHand(null);
        } else {
            inventory.setItemInOffHand(null);
        }
    }
}

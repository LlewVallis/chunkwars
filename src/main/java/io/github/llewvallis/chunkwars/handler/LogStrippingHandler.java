package io.github.llewvallis.chunkwars.handler;

import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Set;

public class LogStrippingHandler implements Listener {

    private final Set<Material> axeMaterials = Set.of(
            Material.WOODEN_AXE,
            Material.STONE_AXE,
            Material.IRON_AXE,
            Material.GOLDEN_AXE,
            Material.DIAMOND_AXE,
            Material.NETHERITE_AXE
    );

    @EventHandler
    private void onLogStrip(PlayerInteractEvent e) {
        Action action = e.getAction();
        ItemStack stack = e.getItem();

        if (stack == null) {
            return;
        }

        Material usedMaterial = stack.getType();

        if (action == Action.RIGHT_CLICK_BLOCK && axeMaterials.contains(usedMaterial)) {
            Material clickedMaterial = e.getClickedBlock().getType();

            if (Tag.LOGS.isTagged(clickedMaterial)) {
                e.setCancelled(true);
            }
        }
    }
}

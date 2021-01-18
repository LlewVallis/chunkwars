package io.github.llewvallis.chunkwars.handler;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Set;

public class PathDiggingHandler implements Listener {

    private final Set<Material> shovelMaterials = Set.of(
            Material.WOODEN_SHOVEL,
            Material.STONE_SHOVEL,
            Material.IRON_SHOVEL,
            Material.GOLDEN_SHOVEL,
            Material.DIAMOND_SHOVEL,
            Material.NETHERITE_SHOVEL
    );

    @EventHandler
    private void onDigPath(PlayerInteractEvent e) {
        Action action = e.getAction();
        ItemStack stack = e.getItem();

        if (stack == null) {
            return;
        }

        Material usedMaterial = stack.getType();

        if (action == Action.RIGHT_CLICK_BLOCK && shovelMaterials.contains(usedMaterial)) {
            Material clickedMaterial = e.getClickedBlock().getType();

            if (clickedMaterial == Material.GRASS_BLOCK) {
                e.setCancelled(true);
            }
        }
    }
}

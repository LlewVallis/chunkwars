package io.github.llewvallis.chunkwars.handler;

import io.github.llewvallis.chunkwars.ChunkWarsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;

public class PotionHandler implements Listener {

    @EventHandler
    private void onDrinkPotion(PlayerItemConsumeEvent e) {
        if (e.getItem().getType() != Material.POTION) {
            return;
        }

        Bukkit.getScheduler().runTaskLater(ChunkWarsPlugin.instance, () -> {
            e.getPlayer().getInventory().remove(Material.GLASS_BOTTLE);
        }, 0);
    }
}

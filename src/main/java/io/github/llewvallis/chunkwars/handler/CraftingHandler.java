package io.github.llewvallis.chunkwars.handler;

import io.github.llewvallis.chunkwars.world.ArenaPool;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;

public class CraftingHandler implements Listener {

    @EventHandler
    private void onCraft(CraftItemEvent e) {
        if (ArenaPool.instance.inArena(e.getWhoClicked())) {
            e.setCancelled(true);
        }
    }
}
